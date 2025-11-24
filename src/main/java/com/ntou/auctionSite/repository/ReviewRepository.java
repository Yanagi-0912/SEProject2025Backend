package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review,String> {
    List<Review> findByUserIDAndProductID(String userID, String productID);
}
