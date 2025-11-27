package com.ntou.auctionSite.controller.user;

import com.ntou.auctionSite.dto.user.AuthResponse;
import com.ntou.auctionSite.dto.user.LoginRequest;
import com.ntou.auctionSite.dto.user.RegisterRequest;
import com.ntou.auctionSite.service.user.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "認證管理", description = "使用者註冊、登入相關 API")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 使用者註冊端點
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @Operation(
            summary = "使用者註冊",
            description = "新使用者註冊帳號。註冊成功後會自動登入並返回 JWT Token。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "註冊成功，返回 JWT Token 和使用者資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"username\":\"john_doe\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "註冊失敗 - 使用者名稱已存在或資料格式錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "使用者名稱已存在")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器內部錯誤",
                    content = @Content(mediaType = "text/plain")
            )
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("註冊失敗: " + e.getMessage());
        }
    }

    /**
     * 使用者登入端點
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(
            summary = "使用者登入",
            description = "使用者登入系統。登入成功後返回 JWT Token，前端需要在後續請求的 Authorization Header 中攜帶此 Token。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "登入成功，返回 JWT Token 和使用者資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"username\":\"john_doe\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "登入失敗 - 帳號或密碼錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "帳號或密碼錯誤")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器內部錯誤",
                    content = @Content(mediaType = "text/plain")
            )
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("帳號或密碼錯誤");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("登入失敗: " + e.getMessage());
        }
    }

    /**
     * 使用者登出端點
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    @Operation(
            summary = "使用者登出",
            description = "登出系統。由於 JWT 是無狀態的，客戶端需要刪除儲存的 Token。" +
                    "Token 會在 30 分鐘後自動過期失效。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "登出成功，請客戶端刪除儲存的 Token",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "登出成功，請刪除客戶端的 Token")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器內部錯誤",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            authService.logout(authHeader);
            return ResponseEntity.ok("登出成功，請刪除客戶端的 Token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("登出失敗: " + e.getMessage());
        }
    }
}
