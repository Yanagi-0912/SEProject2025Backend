package com.ntou.auctionSite.service.user;

import com.ntou.auctionSite.dto.user.UpdatePasswordRequest;
import com.ntou.auctionSite.dto.user.UpdateUserRequest;
import com.ntou.auctionSite.dto.user.UserInfoResponse;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ntou.auctionSite.dto.user.SellerInfoResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用者服務 - 處理使用者資訊的更新
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 獲取使用者資訊
     */
    public UserInfoResponse getUserInfo(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserNickname(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getAverageRating() != null ? user.getAverageRating() : 0.0f,
                user.getRatingCount() != null ? user.getRatingCount() : 0
        );
    }

    /**
     * 更新使用者資訊（名稱、Email、暱稱、地址、電話）
     */
    public UserInfoResponse updateUserInfo(String currentUsername, UpdateUserRequest request) {
        User user = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        // 更新使用者名稱（如果提供）
        if (request.username() != null && !request.username().isBlank()) {
            // 檢查新的使用者名稱是否已被使用
            if (!request.username().equals(currentUsername) &&
                    userRepository.findByUserName(request.username()).isPresent()) {
                throw new RuntimeException("使用者名稱已被使用");
            }
            user.setUserName(request.username());
        }

        // 更新電子郵件（如果提供）
        if (request.email() != null && !request.email().isBlank()) {
            // 檢查新的 email 是否已被使用
            if (!request.email().equals(user.getEmail()) &&
                    userRepository.findByEmail(request.email()).isPresent()) {
                throw new RuntimeException("此電子郵件已被使用");
            }
            user.setEmail(request.email());
        }

        // 更新暱稱（如果提供）
        if (request.nickname() != null && !request.nickname().isBlank()) {
            user.setUserNickname(request.nickname());
        }

        // 更新地址（如果提供）
        if (request.address() != null && !request.address().isBlank()) {
            user.setAddress(request.address());
        }

        // 更新電話號碼（如果提供）
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            user.setPhoneNumber(request.phoneNumber());
        }

        user = userRepository.save(user);

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserNickname(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getAverageRating() != null ? user.getAverageRating() : 0.0f,
                user.getRatingCount() != null ? user.getRatingCount() : 0
        );
    }

    /**
     * 更新使用者密碼
     */
    public void updatePassword(String username, UpdatePasswordRequest request) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        // 驗證目前密碼
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("目前密碼錯誤");
        }

        // 檢查新密碼不能與舊密碼相同
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new RuntimeException("新密碼不能與舊密碼相同");
        }

        // 更新密碼
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /**
     * 取得賣家資訊（包含販售商品列表）
     */
    public SellerInfoResponse getSellerInfo(String sellerId) {
        // 1. 查詢賣家資訊
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("賣家不存在"));

        // 2. 從 User 的 ownedProducts 取得商品列表
        List<SellerInfoResponse.SellerProduct> sellerProducts = new ArrayList<>();

        if (seller.getOwnedProducts() != null) {
            sellerProducts = seller.getOwnedProducts().stream()
                    .map(product -> SellerInfoResponse.SellerProduct.builder()
                            .productId(product.getProductID())
                            .productName(product.getProductName())
                            .price(product.getProductPrice())
                            .imageUrl(product.getProductImage())
                            .status(product.getProductStatus() != null ?
                                    product.getProductStatus().toString() : "UNKNOWN")
                            .build())
                    .toList();
        }

        // 3. 組合賣家資訊（使用正確的欄位名稱）
        return SellerInfoResponse.builder()
                .sellerId(seller.getId())
                .username(seller.getUsername())        // 對應 userName
                .nickname(seller.getUserNickname())    // 對應 userNickname
                .address(seller.getAddress())
                .phoneNumber(seller.getPhoneNumber())
                .averageRating(seller.getAverageRating())
                .ratingCount(seller.getRatingCount())
                .products(sellerProducts)
                .totalProducts(sellerProducts.size())
                .build();
    }

}

