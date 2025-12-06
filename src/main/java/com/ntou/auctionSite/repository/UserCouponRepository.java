package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.user.UserCoupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends MongoRepository<UserCoupon,String> {
    List<UserCoupon> findByUserId(String userId);
}
