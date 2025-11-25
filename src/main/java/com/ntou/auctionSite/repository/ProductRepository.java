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

    List<Product> findByProductType(ProductTypes types);//查找是拍賣類還是直購類
    List<Product> findBySellerIDAndProductName(String sellerID, String productName);
    List<Product> findByProductName(String productName);
    //用於模糊搜尋 ?0options:i表示忽略大小寫
    @Query("{ '$or': [ " +
            "  { 'productName': { $regex: ?0, $options: 'i' } }, " +
            "  { 'productCategory': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Product> searchProducts(String keyword);

}

