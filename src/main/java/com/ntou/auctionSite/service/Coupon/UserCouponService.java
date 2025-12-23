package com.ntou.auctionSite.service.Coupon;

import com.ntou.auctionSite.model.coupon.Coupon;
import com.ntou.auctionSite.model.coupon.UserCoupon;
import com.ntou.auctionSite.repository.CouponRepository;
import com.ntou.auctionSite.repository.UserCouponRepository;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class UserCouponService {

    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private  CouponRepository couponRepository;
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

        if (couponTemplate.getCouponCount() <= 0) {
            throw new IllegalStateException("Coupon template out of stock");
        }

        UserCoupon existing = userCouponRepository.findByUserIdAndCouponID(userId, couponID);
        if (existing != null) {
            existing.setRemainingUsage(existing.getRemainingUsage() + couponTemplate.getMaxUsage());
            existing.setExpireTime(couponTemplate.getExpireTime());
            return userCouponRepository.save(existing);
        }
        else{
            UserCoupon uc = new UserCoupon();
            uc.setId(null);
            uc.setCouponID(couponID);
            uc.setUserId(userId);
            uc.setGetTime(LocalDateTime.now());
            uc.setExpireTime(couponTemplate.getExpireTime());
            uc.setRemainingUsage(couponTemplate.getMaxUsage());
            couponTemplate.setCouponCount(couponTemplate.getCouponCount() - 1);
            couponRepository.save(couponTemplate);
            userCouponRepository.save(uc);
            return uc;
        }
    }
    public List<Coupon> getAvailableCoupon(){
        List<Coupon> availableCoupons = new ArrayList<>();
        for (Coupon c : couponRepository.findAll()) {
            if (c.getCouponCount() > 0 && c.getExpireTime().isAfter(LocalDateTime.now())) {
                availableCoupons.add(c);
            }
        }

        if (availableCoupons.isEmpty()) {
            throw new IllegalStateException("No available coupons to draw");
        }
        return availableCoupons;
    }
    public UserCoupon drawRandomCoupon(String userId) {
        List<Coupon> availableCoupons =getAvailableCoupon();
        int idx = new Random().nextInt(availableCoupons.size());
        Coupon selectedCoupon = availableCoupons.get(idx);
        UserCoupon uc = issueCouponToUser(userId, selectedCoupon.getCouponID());

        return uc;
    }


    public void issueCouponsAfterPay(String buyerID) {

        //首購優惠
        if (userCouponRepository.findByUserId(buyerID).isEmpty()) {
            try{
                issueCouponToUser(buyerID, "COUPC914BDD8");//首購會發放的優惠券
            }
            catch(NoSuchElementException  | IllegalStateException e){
                List<Coupon> couponList=getAvailableCoupon();
                int idx = new Random().nextInt(couponList.size());
                Coupon selectedCoupon = couponList.get(idx);
                issueCouponToUser(buyerID, selectedCoupon.getCouponID());
            }
        }
    }

    public UserCoupon findUserCouponById(String userCouponId){
        // 使用 findById 查詢 UserCoupon 的 id 欄位（不是 couponID）
        // userCouponId 是 UserCoupon 的 id，不是 Coupon 模板的 couponID
        UserCoupon uc = userCouponRepository.findById(userCouponId).orElse(null);
        if (uc==null){
            throw new NoSuchElementException("No coupon found Id: "+userCouponId);
        }
        return uc;
    }
    // 套用優惠券
    public UserCoupon applyCoupon(String userId,String userCouponId, String orderID) {
        UserCoupon uc = findUserCouponById(userCouponId);
        if (!userId.equals(uc.getUserId())) {
            throw new SecurityException("You are not authorized to use this coupon");
        }
        validateUsable(uc);
        uc.setRemainingUsage(uc.getRemainingUsage() - 1);
        uc.setOrderID(orderID);
        uc.setUsedTime(LocalDateTime.now());
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

    private void validateUsable(UserCoupon uc) {
        if (uc.getExpireTime().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Coupon expired");

        if (uc.getRemainingUsage() <= 0)
            throw new IllegalStateException("No remaining usage");
    }


}
