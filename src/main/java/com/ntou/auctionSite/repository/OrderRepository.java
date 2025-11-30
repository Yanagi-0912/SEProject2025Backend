package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.order.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    //?0表示第1個欄位 $elemMatch 會找出陣列中至少有一個物件符合條件
    @Query("{ 'buyerID': ?0, 'orderStatus': ?1, 'orderItems': { $elemMatch: { 'productID': ?2 } } }")
    Optional<Order> findBuyedProduct(String buyerID,Order.OrderStatuses status, String productID);
}
