package com.ntou.auctionSite.service.Coupon;

import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class HolidayCouponScheduler {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserRepository userRepository;
    //秒 分 時 日 月 星期,?表示不指定
    //@Scheduled(fixedRate = 10000)
    @Scheduled(cron = "0 0 0 * * ?")
    public void issueHolidayCoupon(){
        // 節日優惠
        List<String> holidays = Arrays.asList("1/1", "2/28", "4/4", "5/1", "9/28", "10/10", "12/25");
        List<String> testdays = Arrays.asList("12/14","12/19");//純測試，之後可以刪除
        LocalDateTime now = LocalDateTime.now();
        String today = now.getMonthValue() + "/" + now.getDayOfMonth();
        List<User> allUsers = userRepository.findAll();
        if(holidays.contains(today)){
            for (User user : allUsers) {// 依使用者發放優惠券
                System.out.println(user.getId());
                if (user.getId() != null) {
                    System.out.println("發送優惠券成功");
                    userCouponService.issueCouponToUser(user.getId(), "COUP85D2D6D4");
                }
                else {
                    System.err.println("User has null ID: " + user);
                }
            }
        }
    }
}
