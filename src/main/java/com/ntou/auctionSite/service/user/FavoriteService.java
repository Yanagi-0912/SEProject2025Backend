package com.ntou.auctionSite.service.user;

import com.ntou.auctionSite.dto.user.FavoriteItemDTO;
import com.ntou.auctionSite.dto.user.FavoriteResponseDTO;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.user.Favorite;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.FavoriteRepository;
import com.ntou.auctionSite.repository.ProductRepository;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收藏清單服務
 * CRUD 操作：Create（新增）、Read（查詢）、Update（更新）、Delete（刪除）
 */
@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * READ - 取得使用者收藏清單（完整資訊）
     * 查詢時自動過濾已刪除的商品
     *
     * @param userId 使用者 ID（必須是 User._id，不是 userName）
     */
    public FavoriteResponseDTO getUserFavorites(String userId) {
        // 0. 驗證使用者是否存在（防止使用 userName 或不存在的 ID）
        validateUserExists(userId);

        // 1. 從資料庫查詢收藏清單
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElse(new Favorite(userId, userId, new ArrayList<>()));

        // 2. 組合每個項目的完整資訊（商品資訊 + 賣家資訊）
        List<FavoriteItemDTO> items = favorite.getItems().stream()
                .map(item -> {
                    // 查詢商品資訊
                    Product product = productRepository.findById(item.getProductId())
                            .orElse(null); // 如果商品已被刪除，返回 null

                    // 過濾已刪除的商品
                    if (product == null) {
                        return null;
                    }

                    // 查詢賣家資訊
                    User seller = userRepository.findById(product.getSellerID())
                            .orElseThrow(() -> new RuntimeException("賣家不存在: " + product.getSellerID()));

                    // 組合成 DTO
                    return new FavoriteItemDTO(
                            product.getProductID(),
                            product.getProductName(),
                            product.getProductPrice(),
                            product.getProductImage(),
                            product.getProductType().toString(),
                            product.getProductStatus().toString(),
                            seller.getId(),
                            seller.getUsername(),
                            item.getAddedAt()
                    );
                })
                .filter(item -> item != null) // 過濾掉已刪除的商品
                .collect(Collectors.toList());

        return new FavoriteResponseDTO(userId, items, items.size());
    }

    /**
     * CREATE - 將商品加入收藏清單
     * 無收藏上限
     *
     * @param userId 使用者 ID（必須是 User._id，不是 userName）
     * @param productId 商品 ID
     */
    public Favorite addToFavorites(String userId, String productId) {
        // 0. 驗證使用者是否存在
        validateUserExists(userId);

        // 1. 驗證商品是否存在
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));

        // 2. 查詢或建立收藏清單（一對一關係）
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElse(new Favorite(userId, userId, new ArrayList<>()));

        // 檢查商品是否已在收藏清單中
        boolean alreadyExists = favorite.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));

        if (alreadyExists) {
            throw new RuntimeException("商品已在收藏清單中");
        }

        // 加入收藏（會自動記錄 addedAt 時間）
        favorite.getItems().add(new Favorite.FavoriteItem(productId));

        // 儲存到資料庫
        return favoriteRepository.save(favorite);
    }

    /**
     * DELETE - 從收藏清單移除商品
     */
    public Favorite removeFromFavorites(String userId, String productId) {
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("收藏清單不存在"));

        // 移除指定商品
        favorite.getItems().removeIf(item -> item.getProductId().equals(productId));

        return favoriteRepository.save(favorite);
    }

    /**
     * READ - 檢查商品是否在收藏清單中
     */
    public boolean isFavorited(String userId, String productId) {
        Optional<Favorite> favorite = favoriteRepository.findByUserId(userId);

        if (favorite.isEmpty()) {
            return false;
        }

        return favorite.get().getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));
    }

    /**
     * DELETE - 清空收藏清單
     */
    public void clearFavorites(String userId) {
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("收藏清單不存在"));

        favorite.getItems().clear();
        favoriteRepository.save(favorite);
    }

    /**
     * READ - 取得收藏清單的商品數量
     */
    public int getFavoritesCount(String userId) {
        Optional<Favorite> favorite = favoriteRepository.findByUserId(userId);
        return favorite.map(f -> f.getItems().size()).orElse(0);
    }

    // ===== 私有輔助方法 =====

    /**
     * 驗證使用者是否存在
     *
     * @param userId 使用者 ID（必須是 User._id）
     * @throws RuntimeException 如果使用者不存在或傳入的是 userName
     */
    private void validateUserExists(String userId) {
        // 檢查是否傳入空值
        if (userId == null || userId.trim().isEmpty()) {
            throw new RuntimeException("使用者 ID 不可為空");
        }

        // 嘗試用 _id 查詢使用者
        Optional<User> userById = userRepository.findById(userId);

        if (userById.isPresent()) {
            // 找到使用者，驗證通過
            return;
        }

        // 沒找到，檢查是否誤用了 userName
        Optional<User> userByName = userRepository.findByUserName(userId);

        if (userByName.isPresent()) {
            // 使用者存在，但傳入的是 userName 而非 userId
            throw new RuntimeException(
                String.format("請使用使用者 ID 而非使用者名稱。使用者 '%s' 的 ID 是：%s",
                    userId, userByName.get().getId())
            );
        }

        // 使用者不存在
        throw new RuntimeException("使用者不存在: " + userId);
    }
}


