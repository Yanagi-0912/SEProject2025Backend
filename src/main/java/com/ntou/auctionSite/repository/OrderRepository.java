package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    Optional<Order> findByOrderID(String orderID);
}
