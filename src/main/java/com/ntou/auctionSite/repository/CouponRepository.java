package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.coupon.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends MongoRepository<Coupon,String> {
    List<Coupon> findByCouponName(String couponName);//查找是拍賣類還是直購類
}
