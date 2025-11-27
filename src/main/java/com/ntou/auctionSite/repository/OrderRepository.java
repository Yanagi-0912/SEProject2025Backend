package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.order.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    Optional<Order> findByOrderID(String orderID);
    //?0表示第1個欄位
    @Query("{ 'cart.userId': ?0, 'cart.orderStatus': ?1, 'cart.orderItems.productID': ?2 }")
    Optional<Order> findBuyedProduct(String buyerID,Order.OrderStatuses status, String productID);
}
