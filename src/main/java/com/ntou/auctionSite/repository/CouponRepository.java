package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.user.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends MongoRepository<Coupon,String> {

}
