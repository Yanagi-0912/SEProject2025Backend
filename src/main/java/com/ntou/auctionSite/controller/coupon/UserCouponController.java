package com.ntou.auctionSite.controller.coupon;

import com.ntou.auctionSite.model.coupon.UserCoupon;
import com.ntou.auctionSite.service.Coupon.UserCouponService;
import com.ntou.auctionSite.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/api/userCoupon")
@Tag(name = "使用者優惠券管理", description = "使用者個人優惠券操作 API：領取、查詢、套用、刪除")
public class UserCouponController {

    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private UserService userService;

    //取得某使用者所有優惠券
    @GetMapping("/{userId}")
    @Operation(summary = "取得使用者全部優惠券",
            description = "輸入 userId，取得該使用者的所有優惠券清單")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得使用者優惠券列表",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCoupon.class),
                            examples = @ExampleObject(
                                    value = "[{\"couponID\":\"C001\",\"userId\":\"U123\",\"remainingUsage\":1,\"used\":false}]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "沒有找到任何優惠券")
    })

    public ResponseEntity<?> getUserAllCoupons(
            @Parameter(description = "使用者 ID")
            @PathVariable String userId) {
        try {
            List<UserCoupon> list = userCouponService.getUserAllCoupons(userId);
            if (list.isEmpty()) {
                return ResponseEntity.status(404).body("No coupons found for user: " + userId);
            }
            return ResponseEntity.ok(list);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    // 發送優惠券給使用者
    @PostMapping("/issue")
    @Operation(
            summary = "發放優惠券給使用者",
            description = "依照 couponID 範本發放給使用者"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功發放優惠券",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCoupon.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"userId\": \"USER123\",\n" +
                                            "  \"couponID\": \"COUPBFBD3023\",\n" +
                                            "  \"remainingUsage\": 1,\n" +
                                            "  \"used\": false\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "資料格式錯誤"),
            @ApiResponse(responseCode = "403", description = "不可發放優惠券給其他使用者")
    })

    public ResponseEntity<?> issueCouponToUser(
            @Parameter(description = "使用者 ID") @RequestParam String userId,
            @Parameter(description = "優惠券 ID") @RequestParam String couponId,
            Authentication authentication) {
        try {
            String username= authentication.getName(); // 或用 userService 查出完整 User
            String currentUserId = userService.getUserInfo(username).id();
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(403).body("You cannot issue coupon for someone else");
            }
            UserCoupon uc = userCouponService.issueCouponToUser(currentUserId, couponId);
            return ResponseEntity.ok(uc);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body("Issue failed: " + e.getMessage());
        }
    }

    // 抽隨機優惠券給使用者
    @PostMapping("/draw")
    @Operation(
            summary = "抽取隨機優惠券給使用者",
            description = "系統隨機從可用優惠券中抽取一張，發放給指定使用者"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功抽到並發放優惠券",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCoupon.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"userId\": \"USER123\",\n" +
                                            "  \"couponID\": \"COUPBFBD3023\",\n" +
                                            "  \"remainingUsage\": 1,\n" +
                                            "  \"used\": false\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "無可抽的優惠券或發放失敗"),
            @ApiResponse(responseCode = "403", description = "不可為其他使用者抽優惠券")
    })
    public ResponseEntity<?> drawCouponForUser(
            @Parameter(description = "使用者 ID") @RequestParam String userId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            String currentUserId = userService.getUserInfo(username).id();
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(403).body("You cannot draw coupon for someone else");
            }
            UserCoupon uc = userCouponService.drawRandomCoupon(currentUserId);
            return ResponseEntity.ok(uc);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body("Draw failed: " + e.getMessage());
        }
    }

    //結帳套用優惠券
    @PostMapping("/apply")
    @Operation(summary = "結帳時更新優惠券使用次數，這個不用特地叫，已經寫在payorder內了")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功套用優惠券",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCoupon.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"userCouponId\": \"UC0123\",\n" +
                                            "  \"orderID\": \"ORDER9988\",\n" +
                                            "  \"remainingUsage\": 0,\n" +
                                            "  \"used\": true\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "優惠券無法套用（已用過、過期、沒有次數）"),
            @ApiResponse(responseCode = "403", description = "不可套用其他使用者的優惠券")
    })

    public ResponseEntity<?> applyCoupon(
            @Parameter(description = "UserCoupon ID") @RequestParam String userCouponId,
            @Parameter(description = "Order ID") @RequestParam String orderId,
            Authentication authentication) {
        try {
            String username= authentication.getName(); // 或用 userService 查出完整 User
            String currentUserId = userService.getUserInfo(username).id();
            UserCoupon uc = userCouponService.applyCoupon(currentUserId,userCouponId, orderId);
            return ResponseEntity.ok(uc);
        }
        catch (SecurityException e){
            return ResponseEntity.status(403).body("Apply coupon failed: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body("Apply coupon failed: " + e.getMessage());
        }

    }

    //刪除使用者優惠券
    @DeleteMapping("/{userCouponId}")
    @Operation(summary = "刪除使用者優惠券")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功刪除"),
            @ApiResponse(responseCode = "404", description = "找不到該優惠券"),
            @ApiResponse(responseCode = "403", description = "不可刪除其他使用者的優惠券")
    })
    public ResponseEntity<?> deleteUserCoupon(
            @Parameter(description = "使用者優惠券 ID", example = "UC001")
            @PathVariable String userCouponId,
            Authentication authentication) {
        try {
            String username= authentication.getName(); // 或用 userService 查出完整 User
            String currentUserId = userService.getUserInfo(username).id();
            userCouponService.deleteUserCoupon(currentUserId,userCouponId);
            return ResponseEntity.ok("User coupon deleted: " + userCouponId);
        }
        catch (SecurityException e){
            return ResponseEntity.status(403).body("Apply coupon failed: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body("Delete failed: " + e.getMessage());
        }
    }
}
