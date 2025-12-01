package com.ntou.auctionSite.service.product;

import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.ProductRepository;
import com.ntou.auctionSite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 賣家商品管理服務
 * 專門處理賣家的販售商品相關查詢
 *
 * 設計理念：不需要獨立的 SellingProduct collection
 * 直接透過 Product.sellerID 查詢即可
 */
@Service
@RequiredArgsConstructor
public class SellingProductsService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 取得賣家的所有商品
     *
     * @param sellerId 賣家 ID（必須是 User._id）
     */
    public List<Product> getSellerProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerID(sellerId);
    }

    /**
     * 取得賣家的上架商品（ACTIVE）
     */
    public List<Product> getSellerActiveProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDAndProductStatus(
                sellerId,
                Product.ProductStatuses.ACTIVE
        );
    }

    /**
     * 取得賣家的已售出商品
     */
    public List<Product> getSellerSoldProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDAndProductStatus(
                sellerId,
                Product.ProductStatuses.SOLD
        );
    }

    /**
     * 取得賣家的下架商品（INACTIVE）
     */
    public List<Product> getSellerInactiveProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDAndProductStatus(
                sellerId,
                Product.ProductStatuses.INACTIVE
        );
    }

    /**
     * 取得賣家的拍賣商品
     */
    public List<Product> getSellerAuctionProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDAndProductType(
                sellerId,
                ProductTypes.AUCTION
        );
    }

    /**
     * 取得賣家的直購商品
     */
    public List<Product> getSellerDirectProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDAndProductType(
                sellerId,
                ProductTypes.DIRECT
        );
    }

    /**
     * 取得賣家的熱賣商品（依銷售量排序）
     */
    public List<Product> getSellerBestSellingProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDOrderByTotalSalesDesc(sellerId);
    }

    /**
     * 取得賣家最新上架的商品（依建立時間排序）
     */
    public List<Product> getSellerLatestProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDOrderByCreatedTimeDesc(sellerId);
    }

    /**
     * 統計賣家的商品數量
     */
    public int countSellerProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerID(sellerId).size();
    }

    /**
     * 統計賣家的上架商品數量
     */
    public int countSellerActiveProducts(String sellerId) {
        validateSellerExists(sellerId);
        return productRepository.findBySellerIDAndProductStatus(
                sellerId,
                Product.ProductStatuses.ACTIVE
        ).size();
    }

    /**
     * 統計賣家的總銷售量
     */
    public int calculateTotalSales(String sellerId) {
        validateSellerExists(sellerId);
        List<Product> products = productRepository.findBySellerID(sellerId);
        return products.stream()
                .mapToInt(Product::getTotalSales)
                .sum();
    }

    /**
     * 取得賣家商品統計資訊
     */
    public Map<String, Object> getSellerStatistics(String sellerId) {
        validateSellerExists(sellerId);

        List<Product> allProducts = productRepository.findBySellerID(sellerId);

        // 統計各種狀態的商品數量
        long activeCount = allProducts.stream()
                .filter(p -> p.getProductStatus() == Product.ProductStatuses.ACTIVE)
                .count();

        long soldCount = allProducts.stream()
                .filter(p -> p.getProductStatus() == Product.ProductStatuses.SOLD)
                .count();

        long inactiveCount = allProducts.stream()
                .filter(p -> p.getProductStatus() == Product.ProductStatuses.INACTIVE)
                .count();

        // 計算總銷售量
        int totalSales = allProducts.stream()
                .mapToInt(Product::getTotalSales)
                .sum();

        // 計算總瀏覽次數
        int totalViews = allProducts.stream()
                .mapToInt(Product::getViewCount)
                .sum();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalProducts", allProducts.size());
        statistics.put("activeProducts", activeCount);
        statistics.put("soldProducts", soldCount);
        statistics.put("inactiveProducts", inactiveCount);
        statistics.put("totalSales", totalSales);
        statistics.put("totalViews", totalViews);

        return statistics;
    }

    // ===== 私有輔助方法 =====

    /**
     * 驗證賣家是否存在
     *
     * @param sellerId 賣家 ID（必須是 User._id）
     * @throws RuntimeException 如果賣家不存在或傳入的是 userName
     */
    private void validateSellerExists(String sellerId) {
        // 檢查是否傳入空值
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new RuntimeException("賣家 ID 不可為空");
        }

        // 嘗試用 _id 查詢使用者
        if (userRepository.findById(sellerId).isPresent()) {
            return;  // ✅ 驗證通過
        }

        // 沒找到，檢查是否誤用了 userName
        if (userRepository.findByUserName(sellerId).isPresent()) {
            throw new RuntimeException(
                String.format("請使用賣家 ID 而非使用者名稱。使用者 '%s' 的 ID 請透過其他 API 查詢。", sellerId)
            );
        }

        // 賣家不存在
        throw new RuntimeException("賣家不存在: " + sellerId);
    }
}

