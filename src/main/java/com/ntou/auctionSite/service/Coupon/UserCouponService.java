package com.ntou.auctionSite.service.Coupon;

import com.ntou.auctionSite.model.coupon.Coupon;
import com.ntou.auctionSite.model.coupon.UserCoupon;
import com.ntou.auctionSite.repository.UserCouponRepository;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserCouponService {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponService couponService; // 用來查詢優惠券模板
    @Autowired
    private UserRepository userRepository;
    //取得某個user的所有優惠券
    public List<UserCoupon> getUserAllCoupons(String userID){
        List<UserCoupon> userCouponList=userCouponRepository.findByUserId(userID);
        if(!userCouponList.isEmpty()){
            return userCouponList;
        }
        else{
            return userCouponList;
        }
    }
    // 發給指定使用者（使用 Coupon 模板）
    public UserCoupon issueCouponToUser(String userId, String couponID) {

        Coupon couponTemplate = couponService.getCouponById(couponID);

        UserCoupon existing = userCouponRepository.findByUserIdAndCouponID(userId, couponID);
        if (existing != null) {
            existing.setRemainingUsage(existing.getRemainingUsage() + couponTemplate.getMaxUsage());
            existing.setExpireTime(couponTemplate.getExpireTime());
            return userCouponRepository.save(existing);
        }

        UserCoupon uc = new UserCoupon();
        uc.setId(null);
        uc.setCouponID(couponID);
        uc.setUserId(userId);
        uc.setGetTime(LocalDateTime.now());
        uc.setExpireTime(couponTemplate.getExpireTime());
        uc.setRemainingUsage(couponTemplate.getMaxUsage());
        //uc.setUsed(false);

        return userCouponRepository.save(uc);
    }

    public void issueCouponsAfterPay(String buyerID) {
        //首購優惠
        if (userCouponRepository.findByUserId(buyerID).isEmpty()) {
            issueCouponToUser(buyerID, "COUP7EC9E12A");//此優惠券介紹:消費滿不限金額可享8折優惠

        }

    }


    // 套用優惠券
    public UserCoupon applyCoupon(String userId,String userCouponId, String orderID) {
        UserCoupon uc = userCouponRepository.findByCouponID(userCouponId);
        if (uc==null){
            throw new NoSuchElementException("No coupon found Id: "+userCouponId);
        }
        if(!userId.equals(uc.getUserId())){
            throw new SecurityException("You are not authorized to use other user's coupon");
        }
        //if (uc.getUsed())
            //throw new IllegalStateException("Coupon already used");

        if (uc.getExpireTime().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Coupon expired");

        if (uc.getRemainingUsage() <= 0)
            throw new IllegalStateException("No remaining usage");
        uc.setRemainingUsage(uc.getRemainingUsage() - 1);
        uc.setOrderID(orderID);
        uc.setUsedTime(LocalDateTime.now());
        uc.setUsedTime(LocalDateTime.now());
        return userCouponRepository.save(uc);
    }


    // 強制設為使用
    public UserCoupon markCouponUsed(String userId,String userCouponId, String orderID) {

        UserCoupon uc = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("User coupon not found"));
        if(!userId.equals(uc.getUserId())){
            throw new SecurityException("You are not authorized to use other user's coupon");
        }
        //uc.setUsed(true);
        uc.setRemainingUsage(0);
        uc.setUsedTime(LocalDateTime.now());
        uc.setOrderID(orderID);

        return userCouponRepository.save(uc);
    }


    // 刪除使用者優惠券
    public void deleteUserCoupon(String userId,String userCouponId) {
        UserCoupon uc = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("User coupon not found"));
        if(!userId.equals(uc.getUserId())){
            throw new SecurityException("You are not authorized to use other user's coupon");
        }
        userCouponRepository.deleteById(userCouponId);
    }

}
