package com.ntou.auctionSite.controller.user;

import com.ntou.auctionSite.dto.user.UpdatePasswordRequest;
import com.ntou.auctionSite.dto.user.UpdateUserRequest;
import com.ntou.auctionSite.dto.user.UserInfoResponse;
import com.ntou.auctionSite.dto.user.PublicUserInfoResponse;
import com.ntou.auctionSite.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.ntou.auctionSite.dto.user.SellerInfoResponse;

/**
 * 使用者管理 Controller
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "使用者管理", description = "使用者資訊查詢與更新 API - 需要 JWT 驗證")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    /**
     * 取得當前使用者資訊
     * GET /api/user/me
     */
    @GetMapping("/me")
    @Operation(
            summary = "取得當前使用者資訊",
            description = "取得當前登入使用者的詳細資訊"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得使用者資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponse.class),
                            examples = @ExampleObject(
                                    name = "使用者資訊範例",
                                    value = """
                                    {
                                      "id": "507f1f77bcf86cd799439011",
                                      "username": "john_doe",
                                      "email": "john@example.com",
                                      "nickname": "小明",
                                      "address": "台北市中正區",
                                      "phoneNumber": "0912345678",
                                      "averageRating": 4.5,
                                      "ratingCount": 10,
                                      "isBanned": false
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - JWT token 無效或過期"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "使用者不存在"
            )
    })
    public ResponseEntity<UserInfoResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserInfoResponse userInfo = userService.getUserInfo(username);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 更新使用者資訊（名稱、Email、暱稱、地址、電話）
     * PUT /api/user/me
     */
    @PutMapping("/me")
    @Operation(
            summary = "更新使用者資訊",
            description = "更新當前使用者的詳細資訊。所有欄位都是可選的，未提供的欄位保持不變。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功更新使用者資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponse.class),
                            examples = @ExampleObject(
                                    name = "更新後的使用者資訊",
                                    value = """
                                    {
                                      "id": "507f1f77bcf86cd799439011",
                                      "username": "new_username",
                                      "email": "newemail@example.com",
                                      "nickname": "新暱稱",
                                      "address": "台北市中正區羅斯福路一號",
                                      "phoneNumber": "0912345678",
                                      "averageRating": 4.5,
                                      "ratingCount": 10,
                                      "isBanned": false
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "更新失敗 - 使用者名稱或電子郵件已被使用，或資料格式錯誤",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"此電子郵件已被使用\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - JWT token 無效或過期"
            )
    })
    public ResponseEntity<?> updateUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        try {
            String username = authentication.getName();
            UserInfoResponse updatedUser = userService.updateUserInfo(username, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新使用者密碼
     * PUT /api/user/password
     */
    @PutMapping("/password")
    @Operation(
            summary = "更新使用者密碼",
            description = "更新當前使用者的密碼。需要提供目前密碼進行驗證。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "密碼更新成功",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"密碼更新成功\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "更新失敗 - 目前密碼錯誤或新密碼不符合要求",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"目前密碼錯誤\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - JWT token 無效或過期"
            )
    })
    public ResponseEntity<?> updatePassword(
            Authentication authentication,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        try {
            String username = authentication.getName();
            userService.updatePassword(username, request);
            return ResponseEntity.ok(new SuccessResponse("密碼更新成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 根據使用者 ID 取得使用者公開資訊
     * GET /api/user/{userId}
     */
    @GetMapping("/{userId}")
    @Operation(
            summary = "取得使用者公開資訊",
            description = """
                    根據使用者 ID 取得該使用者的公開資訊。
                    
                    **用途**：
                    - 查看其他使用者的基本資料
                    - 聊天功能顯示對方資訊
                    - 商品頁面顯示賣家資訊
                    
                    **注意**：此 API 不需要登入，且不會返回敏感資訊（如 email、isBanned）
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得使用者資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PublicUserInfoResponse.class),
                            examples = @ExampleObject(
                                    name = "使用者公開資訊範例",
                                    value = """
                                    {
                                      "id": "507f1f77bcf86cd799439011",
                                      "username": "john_doe",
                                      "nickname": "小明",
                                      "address": "台北市中正區",
                                      "phoneNumber": "0912345678",
                                      "averageRating": 4.5,
                                      "ratingCount": 10
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "使用者不存在"
            )
    })
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            PublicUserInfoResponse userInfo = userService.getPublicUserInfo(userId);
            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 根據使用者 ID 取得賣家資訊
     * GET /api/user/{userId}/seller
     */
    @GetMapping("/{userId}/seller")
    @Operation(
            summary = "取得賣家資訊",
            description = "根據指定的使用者 ID 取得該賣家的公開資訊及販售商品列表"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得賣家資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SellerInfoResponse.class),
                            examples = @ExampleObject(
                                    name = "賣家資訊範例",
                                    value = """
                                {
                                  "sellerId": "507f1f77bcf86cd799439011",
                                  "username": "apple_store",
                                  "nickname": "Apple 專賣店",
                                  "address": "台北市信義區",
                                  "phoneNumber": "0912345678",
                                  "averageRating": 4.8,
                                  "ratingCount": 152,
                                  "totalProducts": 5,
                                  "products": [
                                    {
                                      "productId": "P001",
                                      "productName": "iPhone 15 Pro",
                                      "price": 35900,
                                      "imageUrl": "/uploads/iphone15.jpg",
                                      "status": "AVAILABLE"
                                    }
                                  ]
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "賣家不存在"
            )
    })
    public ResponseEntity<?> getSellerInfo(@PathVariable String userId) {
        try {
            SellerInfoResponse sellerInfo = userService.getSellerInfo(userId);
            return ResponseEntity.ok(sellerInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }


    // 內部類別用於回應
    private record ErrorResponse(String error) {}
    private record SuccessResponse(String message) {}
}

