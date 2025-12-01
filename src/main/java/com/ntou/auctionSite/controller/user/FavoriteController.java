package com.ntou.auctionSite.controller.user;

import com.ntou.auctionSite.dto.user.FavoriteResponseDTO;
import com.ntou.auctionSite.dto.user.SimpleFavoriteResponseDTO;
import com.ntou.auctionSite.service.user.FavoriteService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏清單 Controller
 * 提供基本 CRUD 操作：
 * - CREATE: 新增收藏
 * - READ: 查詢收藏清單
 * - DELETE: 刪除收藏、清空收藏
 */
@RestController
@RequestMapping("/api/favorites")
@Tag(name = "收藏清單管理", description = "使用者收藏清單相關 API（CRUD 操作）")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // ===== READ 操作 =====

    @GetMapping("/{userId}")
    @Operation(
            summary = "【READ】取得使用者收藏清單",
            description = "取得指定使用者的完整收藏清單（包含商品詳細資訊、賣家資訊），自動過濾已刪除的商品"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得收藏清單",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "userId": "U001",
                                      "items": [
                                        {
                                          "productId": "P001",
                                          "productName": "iPhone 15 Pro",
                                          "productPrice": 39900,
                                          "productImage": "https://example.com/iphone15.jpg",
                                          "productType": "DIRECT",
                                          "productStatus": "ACTIVE",
                                          "sellerId": "U002",
                                          "sellerName": "AppleStore",
                                          "addedAt": "2025-11-29T10:30:00"
                                        }
                                      ],
                                      "totalItems": 1
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<FavoriteResponseDTO> getUserFavorites(
            @Parameter(description = "使用者 ID", example = "U001", required = true)
            @PathVariable String userId) {
        try {
            FavoriteResponseDTO response = favoriteService.getUserFavorites(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("取得收藏清單失敗: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/simple")
    @Operation(
            summary = "【READ】取得使用者收藏清單（簡化版）",
            description = "只回傳商品 ID 和加入時間，不查詢商品詳細資訊。適合用於快速檢查收藏狀態或前端快取。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得簡化收藏清單",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SimpleFavoriteResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "userId": "U001",
                                      "items": [
                                        {
                                          "productId": "P001",
                                          "addedAt": "2025-11-29T10:30:00"
                                        },
                                        {
                                          "productId": "P005",
                                          "addedAt": "2025-11-28T15:20:00"
                                        }
                                      ],
                                      "totalItems": 2
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<SimpleFavoriteResponseDTO> getSimpleUserFavorites(
            @Parameter(description = "使用者 ID", example = "U001", required = true)
            @PathVariable String userId) {
        try {
            SimpleFavoriteResponseDTO response = favoriteService.getSimpleUserFavorites(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("取得收藏清單失敗: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/check/{productId}")
    @Operation(
            summary = "【READ】檢查是否已收藏",
            description = "檢查指定商品是否在使用者的收藏清單中，常用於商品頁面顯示「已收藏」狀態"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功查詢",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "已收藏", value = "true"),
                                    @ExampleObject(name = "未收藏", value = "false")
                            }
                    )
            )
    })
    public ResponseEntity<Boolean> isFavorited(
            @Parameter(description = "使用者 ID", example = "U001", required = true)
            @PathVariable String userId,
            @Parameter(description = "商品 ID", example = "P001", required = true)
            @PathVariable String productId) {
        boolean isFavorited = favoriteService.isFavorited(userId, productId);
        return ResponseEntity.ok(isFavorited);
    }

    @GetMapping("/{userId}/count")
    @Operation(
            summary = "【READ】取得收藏數量",
            description = "取得使用者的收藏商品總數，可用於顯示收藏徽章"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得數量",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "5")
                    )
            )
    })
    public ResponseEntity<Integer> getFavoritesCount(
            @Parameter(description = "使用者 ID", example = "U001", required = true)
            @PathVariable String userId) {
        int count = favoriteService.getFavoritesCount(userId);
        return ResponseEntity.ok(count);
    }

    // ===== CREATE 操作 =====

    @PostMapping("/{userId}/items/{productId}")
    @Operation(
            summary = "【CREATE】加入收藏清單",
            description = "將指定商品加入使用者的收藏清單（無收藏上限），會自動記錄加入時間"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功加入收藏",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "成功加入收藏清單",
                                      "success": true
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "加入失敗（商品不存在或已在收藏清單中）",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "商品不存在",
                                            value = """
                                            {
                                              "message": "商品不存在: P001",
                                              "success": false
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "已在收藏中",
                                            value = """
                                            {
                                              "message": "商品已在收藏清單中",
                                              "success": false
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> addToFavorites(
            @Parameter(description = "使用者 ID", example = "U001", required = true)
            @PathVariable String userId,
            @Parameter(description = "商品 ID", example = "P001", required = true)
            @PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            favoriteService.addToFavorites(userId, productId);
            response.put("message", "成功加入收藏清單");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===== DELETE 操作 =====

    @DeleteMapping("/{userId}/items/{productId}")
    @Operation(
            summary = "【DELETE】移除收藏",
            description = "從收藏清單中移除指定商品"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功移除",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "已從收藏清單移除",
                                      "success": true
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "移除失敗（收藏清單不存在）",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "收藏清單不存在",
                                      "success": false
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> removeFromFavorites(
            @Parameter(description = "使用者 ID", example = "U001", required = true)
            @PathVariable String userId,
            @Parameter(description = "商品 ID", example = "P001", required = true)
            @PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            favoriteService.removeFromFavorites(userId, productId);
            response.put("message", "已從收藏清單移除");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(
            summary = "【DELETE】清空收藏清單",
            description = "清空使用者的所有收藏"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功清空",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "收藏清單已清空",
                                      "success": true
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "清空失敗",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "收藏清單不存在",
                                      "success": false
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> clearFavorites(
            @Parameter(description = "使用者 ID", example = "U001", required = true)
            @PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            favoriteService.clearFavorites(userId);
            response.put("message", "收藏清單已清空");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }
    }
}


