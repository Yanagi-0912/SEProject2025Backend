package com.ntou.auctionSite.repository;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    // ===== 基本查詢 =====
    List<Product> findBySellerID(String sellerID);
    List<Product> findByProductType(ProductTypes types);//查找是拍賣類還是直購類
    List<Product> findBySellerIDAndProductName(String sellerID, String productName);
    List<Product> findByProductName(String productName);

    // ===== 模糊搜尋 =====
    //用於模糊搜尋 ?0options:i表示忽略大小寫
    @Query("{ '$or': [ " +
            "  { 'productName': { $regex: ?0, $options: 'i' } }, " +
            "  { 'productCategory': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Product> searchProducts(String keyword);

    // ===== Selling Products 相關查詢 =====
    /**
     * 根據賣家 ID 和商品狀態查詢商品
     * 用途：查詢賣家的上架商品、已售出商品等
     */
    List<Product> findBySellerIDAndProductStatus(String sellerID, Product.ProductStatuses status);

    /**
     * 根據賣家 ID 和商品類型查詢商品
     * 用途：查詢賣家的拍賣商品或直購商品
     */
    List<Product> findBySellerIDAndProductType(String sellerID, ProductTypes type);

    /**
     * 查詢賣家的商品並按銷售量排序
     * 用途：查看賣家的熱賣商品
     */
    List<Product> findBySellerIDOrderByTotalSalesDesc(String sellerID);

    /**
     * 查詢賣家的商品並按建立時間排序
     * 用途：查看賣家最新上架的商品
     */
    List<Product> findBySellerIDOrderByCreatedTimeDesc(String sellerID);

}

