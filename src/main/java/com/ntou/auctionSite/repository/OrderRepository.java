package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.order.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    //?0表示第1個欄位 $elemMatch 會找出陣列中至少有一個物件符合條件
    @Query("{ 'buyerID': ?0, 'orderStatus': ?1, 'orderItems': { $elemMatch: { 'productID': ?2 } } }")
    List<Order> findBuyedProduct(String buyerID,Order.OrderStatuses status, String productID);
    List<Order> findByBuyerID(String buyerID);
    
    // 根據 orderID 欄位查詢訂單（因為 @Id 標註在 orderID 上，但為了確保查詢正確，使用明確的查詢方法）
    Optional<Order> findByOrderID(String orderID);
}
