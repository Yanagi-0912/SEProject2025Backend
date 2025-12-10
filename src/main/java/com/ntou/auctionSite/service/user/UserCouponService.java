package com.ntou.auctionSite.service.user;

import com.ntou.auctionSite.model.coupon.Coupon;
import com.ntou.auctionSite.model.coupon.UserCoupon;
import com.ntou.auctionSite.repository.OrderRepository;
import com.ntou.auctionSite.repository.UserCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class UserCouponService {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CouponService couponService; // 用來查詢優惠券模板


    // 發給指定使用者（使用 Coupon 模板）
    public UserCoupon issueCouponToUser(String userId, String couponID) {

        Coupon couponTemplate = couponService.getCouponById(couponID);

        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponID(couponID);

        uc.setGetTime(LocalDateTime.now());
        uc.setExpireTime(couponTemplate.getExpireTime());
        uc.setRemainingUsage(couponTemplate.getMaxUsage());

        uc.setUsed(false);

        return userCouponRepository.save(uc);
    }


    // 首購優惠
    public UserCoupon issueFirstBuyCoupon(String userID) {

        if (!orderRepository.findByBuyerID(userID).isEmpty())
            return null;

        // 你的資料庫中必須已有一張模板，如：COUP_FIRSTBUY
        return issueCouponToUser(userID, "COUP_FIRSTBUY");
    }


    // 特定節日發放免運券給所有使用者
    public UserCoupon issueHolidayCoupon(String userID) {

        List<String> holidays = Arrays.asList("1/1", "2/28", "4/4", "5/1", "9/28", "10/10", "12/25");

        LocalDateTime now = LocalDateTime.now();
        String today = now.getMonthValue() + "/" + now.getDayOfMonth();

        if (!holidays.contains(today))
            return null;

        // 假設你的模板中有一張：COUP_FREESHIP
        return issueCouponToUser(userID, "COUP85D2D6D4");
    }


    // 查詢使用者所有優惠券
    public List<UserCoupon> getAllCoupons(String userId) {
        return userCouponRepository.findByUserId(userId);
    }


    // 套用優惠券
    public UserCoupon applyCoupon(String userCouponId, String orderID) {

        UserCoupon uc = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("User coupon not found"));

        if (uc.isUsed())
            throw new IllegalStateException("Coupon already used");

        if (uc.getExpireTime().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Coupon expired");

        if (uc.getRemainingUsage() <= 0)
            throw new IllegalStateException("No remaining usage");

        uc.setRemainingUsage(uc.getRemainingUsage() - 1);
        uc.setOrderID(orderID);

        if (uc.getRemainingUsage() == 0) {
            uc.setUsed(true);
            uc.setUsedTime(LocalDateTime.now());
        }

        return userCouponRepository.save(uc);
    }


    // 強制設為使用
    public UserCoupon markCouponUsed(String userCouponId, String orderID) {

        UserCoupon uc = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("User coupon not found"));

        uc.setUsed(true);
        uc.setRemainingUsage(0);
        uc.setUsedTime(LocalDateTime.now());
        uc.setOrderID(orderID);

        return userCouponRepository.save(uc);
    }


    // 刪除使用者優惠券
    public void deleteUserCoupon(String userCouponId) {
        userCouponRepository.deleteById(userCouponId);
    }

}
