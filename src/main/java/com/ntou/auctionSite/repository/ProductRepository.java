package com.ntou.auctionSite.repository;
import com.ntou.auctionSite.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    //Optional<Product> findByProductID(String ProductID); //自訂查詢方法:查"ProductID"
}

