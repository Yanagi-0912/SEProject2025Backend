package com.ntou.auctionSite.controller.user;

import com.ntou.auctionSite.service.user.LotteryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lottery")
@Tag(name = "抽獎管理", description = "使用者抽獎相關 API - 扣除抽獎次數並返回剩餘次數")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    @PostMapping("/useOnce")
    @Operation(summary = "使用一次抽獎", description = "扣除使用者一次抽獎次數並返回剩餘抽獎次數")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功扣除抽獎次數，返回剩餘次數"),
            @ApiResponse(responseCode = "401", description = "未授權，請先登入"),
            @ApiResponse(responseCode = "400", description = "抽獎次數已用完"),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> useLottery(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized: please login");
        }
        String userName = authentication.getName();
        try {

            int remaining = lotteryService.reduceDrawTimes(userName);
            return ResponseEntity.ok("Remaining draw times: " + remaining);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("No more draw time: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}
