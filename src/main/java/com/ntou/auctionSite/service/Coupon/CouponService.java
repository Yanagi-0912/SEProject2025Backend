package com.ntou.auctionSite.service.Coupon;

import com.ntou.auctionSite.model.coupon.Coupon;
import com.ntou.auctionSite.model.coupon.CouponType;
import com.ntou.auctionSite.repository.CouponRepository;
import com.ntou.auctionSite.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CouponService {//管理通用的優惠券
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private OrderRepository orderRepository;
    public Coupon getCouponById(String couponID) {
        return couponRepository.findById(couponID)
                .orElseThrow(() -> new NoSuchElementException("Coupon not found with couponID: " + couponID));
    }
    public List<Coupon> getAllCoupons(){
        List<Coupon> couponList= couponRepository.findAll();
        if(!couponList.isEmpty()){
            return couponList;
        }
        else{
            throw new NoSuchElementException("Coupon not found");
        }
    }

    public Coupon createCoupon(Coupon coupon) {
        if (coupon == null) {
            throw new IllegalArgumentException("Coupon cannot be null");
        }
        String randomId;
        coupon.setCreatedTime(LocalDateTime.now());
        // 驗證欄位合法性
        validateCouponBeforeCreate(coupon);
        do {
            randomId = "COUP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();//先用8位就好
        }
        while (couponRepository.findById(randomId).isPresent());
        coupon.setCouponID(randomId);
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(String couponID){
        if(couponRepository.findById(couponID).isEmpty()){
            throw new NoSuchElementException("Coupon not found with couponID: "+couponID);
        }
        couponRepository.deleteById(couponID);
    }

    private void validateCouponBeforeCreate(Coupon coupon) {
        // 必填：couponName
        if (coupon.getCouponName() == null || coupon.getCouponName().trim().isEmpty()) {
            throw new IllegalArgumentException("couponName is required");
        }

        // discountValue/discountAmount 檢查（視你的欄位命名）
        // 這裡我示範 discountValue 為折扣，discountType 為 "PERCENT" 或 "FIXED" 或 "FREESHIP"
        CouponType type = coupon.getDiscountType();
        Double value = coupon.getDiscountValue();

        if (type != null) {
            if (type!=CouponType.FIXED&&type!=CouponType.PERCENT&&
                    type!=CouponType.FREESHIP&&type!=CouponType.BUY_ONE_GET_ONE) {
                throw new IllegalArgumentException("Illegal discountType");
            }
            List<Coupon> existing = couponRepository.findByCouponName(coupon.getCouponName());//禁止創建同名優惠券
            if(!existing.isEmpty()) {
                throw new IllegalStateException("The coupon with the same name already exists！");
            }
            //買一送一不需要discountValue
            if (type.equals(CouponType.BUY_ONE_GET_ONE)) {
                coupon.setDiscountValue(null);
                return;
            }

            // 免運券不需要discountValue
            if (type.equals(CouponType.FREESHIP)) {
                return;
            }
            if (value == null) {
                throw new IllegalArgumentException("discountValue is required for discount types other than FREESHIP");
            }
            if (type.equals(CouponType.PERCENT)) {//XX折優惠券
                if (value == 0.0 || value >= 1.0) {
                    throw new IllegalArgumentException("For PERCENT discountValue must be >0% and <100%");
                }
            }
            if (type.equals(CouponType.FIXED)) { // 折XX元
                if (value <= 0) {
                    throw new IllegalArgumentException("For FIXED discountValue must be > 0");
                }
            }

        }
        else {
            // 若沒有指定 discountType，至少要有 discountValue（保險）
            if (value == null || value <= 0) {
                throw new IllegalArgumentException("Either discountType or positive discountValue must be provided");
            }
        }

        if (coupon.getMinPurchaseAmount() != null && coupon.getMinPurchaseAmount() < 0) {
            throw new IllegalArgumentException("minPurchaseAmount cannot be negative");
        }

        if (coupon.getMaxUsage() < 0) {
            throw new IllegalArgumentException("maxUsage cannot be negative");
        }
        if (coupon.getCouponCount() < 0) {
            throw new IllegalArgumentException("couponCount cannot be negative");
        }
        if (coupon.getExpireTime() != null) {
            if (coupon.getExpireTime().isBefore(coupon.getCreatedTime())) {
                throw new IllegalArgumentException("expireTime must be in the future");
            }
        }
        else{//預設在創建的30天後過期
            coupon.setExpireTime(coupon.getCreatedTime().plusDays(30));
        }
    }

}
