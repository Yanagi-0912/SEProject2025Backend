package com.ntou.auctionSite.service.user;

import com.ntou.auctionSite.dto.user.UserInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LotteryService {
    @Autowired
    UserService userService;

    public int reduceDrawTimes(String username) {
        UserInfoResponse userdata = userService.getUserInfo(username);
        int remainingDrawTimes = userdata.remainingDrawTimes();
        if (remainingDrawTimes <= 0) {
            throw new IllegalStateException("No more draws for coupon!");
        }

        remainingDrawTimes -= 1;
        userService.updateRemainingDrawTimes(username, remainingDrawTimes);

        return remainingDrawTimes;
    }

    public int addDrawTimes(String username, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("新增次數必須大於 0");
        }
        UserInfoResponse userdata = userService.getUserInfo(username);
        int remaining = userdata.remainingDrawTimes() + count;
        userService.updateRemainingDrawTimes(username, remaining);
        return remaining;
    }
}

