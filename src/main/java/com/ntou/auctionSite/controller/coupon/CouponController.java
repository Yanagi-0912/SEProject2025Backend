package com.ntou.auctionSite.controller.coupon;

import com.ntou.auctionSite.model.coupon.Coupon;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.service.user.CouponService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/coupons")
@Tag(name = "優惠券管理", description = "優惠券模板API，在這裡創建與刪除未來發給使用者的優惠券")
public class CouponController {
    @Autowired
    CouponService couponService;
    @GetMapping
    @Operation(
            summary = "取得所有優惠券",
            description = "回傳系統內所有可使用的優惠券類型"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得優惠券列表",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Coupon.class),
                            examples = @ExampleObject(
                                    value = "[{\"couponID\":\"C001\",\"couponName\":\"首購85折\",\"discount\":0.85}]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "找不到該優惠券"),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getAllCoupons() {
        try {
            List<Coupon> coupons = couponService.getAllCoupons();
            return ResponseEntity.ok(coupons);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching coupons: " + e.getMessage());
        }
    }


    // 根據 couponID 查詢
    @GetMapping("/{couponID}")
    @Operation(summary = "根據 ID 查詢優惠券", description = "輸入 couponID 查詢優惠券詳細資料")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得優惠券資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Coupon.class),
                            examples = @ExampleObject(
                                    value = "{\"couponID\":\"C001\",\"couponName\":\"首購85折\",\"discount\":0.85}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "找不到該優惠券"),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getCouponById(
            @Parameter(description = "優惠券ID", example = "COUPBFBD3023")
            @PathVariable String couponID
    ) {
        try {
            Coupon coupon = couponService.getCouponById(couponID);
            return ResponseEntity.ok(coupon);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Coupon not found: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }


    // 建立新優惠券
    @PostMapping
    @Operation(summary = "建立新的優惠券")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "成功建立優惠券",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Coupon.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"couponID\": \"C001\",\n" +
                                            "  \"couponName\": \"85折優惠券\",\n" +
                                            "  \"description\": \"結帳可享85折\",\n" +
                                            "  \"expireTime\": \"2025-12-31T23:59:59\",\n" +
                                            "  \"couponCount\": 1000,\n" +
                                            "  \"discountType\": \"PERCENT\",\n" +
                                            "  \"discountValue\": 0.85,\n" +
                                            "  \"minPurchaseAmount\": 200,\n" +
                                            "  \"used\": false,\n" +
                                            "  \"usedTime\": null,\n" +
                                            "  \"createdTime\": \"2025-01-01T12:00:00\",\n" +
                                            "  \"maxUsage\": 1\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "輸入資料格式錯誤"),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })

    public ResponseEntity<?> createCoupon(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "優惠券資料",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    name = "新增優惠券範例",
                                    value = "{\n" +
                                            "  \"couponName\": \"85折優惠券\",\n" +
                                            "  \"description\": \"結帳可享85折(低消200)\",\n" +
                                            "  \"expireTime\": \"2025-12-31T23:59:59\",\n" +
                                            "  \"couponCount\": 1000,\n" +
                                            "  \"discountType\": \"PERCENT\",\n" +
                                            "  \"discountValue\": 0.85,\n" +
                                            "  \"minPurchaseAmount\": 200,\n" +
                                            "  \"used\": false,\n" +
                                            "  \"usedTime\": null,\n" +
                                            "  \"createdTime\": \"2025-01-01T12:00:00\",\n" +
                                            "  \"maxUsage\": 1\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Coupon coupon) {
        try {
            Coupon saved = couponService.createCoupon(coupon);
            return ResponseEntity.status(201).body(saved);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400).body("Invalid coupon data: " + e.getMessage());
        }

        catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating coupon: " + e.getMessage());
        }
    }


    // 刪除優惠券
    @DeleteMapping("/{couponID}")
    @Operation(summary = "刪除優惠券")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功刪除優惠券"),
            @ApiResponse(responseCode = "404", description = "找不到該優惠券"),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> deleteCoupon(
            @Parameter(description = "優惠券ID", example = "COUPBFBD3023")
            @PathVariable String couponID) {
        try {
            couponService.deleteCoupon(couponID);
            return ResponseEntity.ok("Coupon deleted");
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Coupon not found: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting coupon: " + e.getMessage());
        }
    }
}