package com.ntou.auctionSite.controller.history;

import com.ntou.auctionSite.dto.history.*;
import com.ntou.auctionSite.model.history.*;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.UserRepository;
import com.ntou.auctionSite.service.history.HistoryService;
import com.ntou.auctionSite.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@Tag(name = "歷史記錄管理", description = "使用者歷史記錄相關 API - 包含瀏覽、競標、購買、評論歷史")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.ntou.auctionSite.service.product.ProductService productService;

    // ===== 通用歷史記錄查詢 =====

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "查詢使用者所有歷史記錄",
        description = "根據使用者 ID 查詢該使用者的所有類型歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得歷史記錄",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = History.class))),
        @ApiResponse(responseCode = "404", description = "找不到該使用者的歷史記錄"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getAllHistoriesByUserId(
            @Parameter(description = "使用者 ID", required = true)
            @PathVariable String userId) {
        try {
            List<History> histories = historyService.searchAllHistoriesByUserId(userId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到使用者的歷史記錄",
                    "userId", userId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{historyId}")
    @Operation(
        summary = "根據歷史記錄 ID 查詢",
        description = "根據歷史記錄 ID 查詢特定的歷史記錄（支援所有子類型）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得歷史記錄",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = History.class))),
        @ApiResponse(responseCode = "404", description = "找不到該歷史記錄"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getHistoryById(
            @Parameter(description = "歷史記錄 ID", required = true)
            @PathVariable String historyId) {
        try {
            var opt = historyService.findHistoryByIdAcrossAllTypes(historyId);
            if (opt.isPresent()) {
                return ResponseEntity.ok(opt.get());
            }
            return ResponseEntity.status(404).body(java.util.Map.of(
                "status", 404,
                "message", "找不到歷史記錄",
                "historyId", historyId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    // ===== 競標歷史 =====

    @GetMapping("/bid/user/{userId}")
    @Operation(
        summary = "查詢使用者競標歷史",
        description = "根據使用者 ID 查詢該使用者的所有競標歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得競標歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = bidHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該使用者的競標歷史"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getBidHistoriesByUserId(
            @Parameter(description = "使用者 ID", required = true)
            @PathVariable String userId) {
        try {
            List<bidHistory> histories = historyService.getBidHistoriesByUserId(userId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到使用者的競標歷史",
                    "userId", userId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @GetMapping("/bid/product/{productId}")
    @Operation(
        summary = "查詢商品競標歷史",
        description = "根據商品 ID 查詢該商品的所有競標歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得競標歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = bidHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該商品的競標歷史"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getBidHistoriesByProductId(
            @Parameter(description = "商品 ID", required = true)
            @PathVariable String productId) {
        try {
            List<bidHistory> histories = historyService.getBidHistoriesByProductId(productId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到商品的競標歷史",
                    "productId", productId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/bid")
    @Operation(
        summary = "新增競標歷史記錄",
        description = "創建新的競標歷史記錄（userID 從 JWT token 中自動取得，historyItem 由系統自動設定）"
    )
    public ResponseEntity<?> createBidHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateBidHistoryRequest request) {
        try {
            // 從 JWT token 中提取 username，然後查詢 userID
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
            String userID = user.getId();

            // 建立歷史記錄
            bidHistory history = new bidHistory(userID, request.getProductID(), request.getBidAmount());

            // 查詢產品並設定 historyItem
            try {
                com.ntou.auctionSite.model.product.Product product = productService.getProductById(request.getProductID());
                if (product != null) {
                    HistoryItem item = new HistoryItem(
                        request.getProductID(),
                        product.getSellerID(),
                        product.getProductName(),
                        product.getProductCategory(),
                        product.getProductPrice(),
                        1,
                        product.getProductPrice()
                    );
                    history.setHistoryItem(item);
                }
            } catch (Exception e) {
                // 產品不存在或查詢失敗，historyItem 保持為 null
            }

            bidHistory saved = historyService.saveBidHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    // ===== 瀏覽歷史 =====

    @GetMapping("/browse/user/{userId}")
    @Operation(
        summary = "查詢使用者瀏覽歷史",
        description = "根據使用者 ID 查詢該使用者的所有瀏覽歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得瀏覽歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = browseHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該使用者的瀏覽歷史"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getBrowseHistoriesByUserId(
            @Parameter(description = "使用者 ID", required = true)
            @PathVariable String userId) {
        try {
            List<browseHistory> histories = historyService.getBrowseHistoriesByUserId(userId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到使用者的瀏覽歷史",
                    "userId", userId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @GetMapping("/browse/product/{productId}")
    @Operation(
        summary = "查詢商品瀏覽歷史",
        description = "根據商品 ID 查詢該商品的所有瀏覽歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得瀏覽歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = browseHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該商品的瀏覽歷史"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getBrowseHistoriesByProductId(
            @Parameter(description = "商品 ID", required = true)
            @PathVariable String productId) {
        try {
            List<browseHistory> histories = historyService.getBrowseHistoriesByProductId(productId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到商品的瀏覽歷史",
                    "productId", productId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/browse")
    @Operation(
        summary = "新增瀏覽歷史記錄",
        description = "創建新的瀏覽歷史記錄（userID 從 JWT token 中自動取得，historyItem 由系統自動設定）"
    )
    public ResponseEntity<?> createBrowseHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateBrowseHistoryRequest request) {
        try {
            // 從 JWT token 中提取 username，然後查詢 userID
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
            String userID = user.getId();

            // 建立歷史記錄
            browseHistory history = new browseHistory(userID, request.getProductID());

            // 查詢產品並設定 historyItem
            try {
                com.ntou.auctionSite.model.product.Product product = productService.getProductById(request.getProductID());
                if (product != null) {
                    HistoryItem item = new HistoryItem(
                        request.getProductID(),
                        product.getSellerID(),
                        product.getProductName(),
                        product.getProductCategory(),
                        product.getProductPrice(),
                        1,
                        product.getProductPrice()
                    );
                    history.setHistoryItem(item);
                }
            } catch (Exception e) {
                // 產品不存在或查詢失敗，historyItem 保持為 null
            }

            browseHistory saved = historyService.saveBrowseHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    // ===== 購買歷史 =====

    @GetMapping("/purchase/user/{userId}")
    @Operation(
        summary = "查詢使用者購買歷史",
        description = "根據使用者 ID 查詢該使用者的所有購買歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得購買歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = purchaseHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該使用者的購買歷史"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getPurchaseHistoriesByUserId(
            @Parameter(description = "使用者 ID", required = true)
            @PathVariable String userId) {
        try {
            List<purchaseHistory> histories = historyService.getPurchaseHistoriesByUserId(userId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到使用者的購買歷史",
                    "userId", userId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @GetMapping("/purchase/product/{productId}")
    @Operation(
        summary = "查詢商品購買歷史",
        description = "根據商品 ID 查詢包含該商品的所有購買歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得購買歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = purchaseHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該商品的購買歷史"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getPurchaseHistoriesByProductId(
            @Parameter(description = "商品 ID", required = true)
            @PathVariable String productId) {
        try {
            List<purchaseHistory> histories = historyService.getPurchaseHistoriesByProductId(productId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到商品的購買歷史",
                    "productId", productId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/purchase")
    @Operation(
        summary = "新增購買歷史記錄",
        description = "創建新的購買歷史記錄（userID 從 JWT token 中自動取得，historyItems 由系統自動設定）"
    )
    public ResponseEntity<?> createPurchaseHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreatePurchaseHistoryRequest request) {
        try {
            // 從 JWT token 中提取 username，然後查詢 userID
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
            String userID = user.getId();

            // 建立歷史記錄
            purchaseHistory history = new purchaseHistory(userID, request.getProductID(), request.getProductQuantity());

            // 查詢所有產品並設定 historyItems
            java.util.ArrayList<HistoryItem> items = new java.util.ArrayList<>();
            try {
                for (String productId : request.getProductID()) {
                    com.ntou.auctionSite.model.product.Product product = productService.getProductById(productId);
                    if (product != null) {
                        HistoryItem item = new HistoryItem(
                            productId,
                            product.getSellerID(),
                            product.getProductName(),
                            product.getProductCategory(),
                            product.getProductPrice(),
                            request.getProductQuantity(),
                            product.getProductPrice() * request.getProductQuantity()
                        );
                        items.add(item);
                    }
                }
            } catch (Exception e) {
                // 產品不存在或查詢失敗，繼續處理
            }

            if (!items.isEmpty()) {
                history.setHistoryItems(items);
            }

            purchaseHistory saved = historyService.savePurchaseHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    // ===== 評論歷史 =====

    @GetMapping("/review/user/{userId}")
    @Operation(
        summary = "查詢使用者評論歷史",
        description = "根據使用者 ID 查詢該使用者的所有評論歷史記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得評論歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = reviewHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該使用者的評論歷史"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getReviewHistoriesByUserId(
            @Parameter(description = "使用者 ID", required = true)
            @PathVariable String userId) {
        try {
            List<reviewHistory> histories = historyService.getReviewHistoriesByUserId(userId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到使用者的評論歷史",
                    "userId", userId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/review/{reviewId}")
    @Operation(
        summary = "查詢評論的歷史記錄",
        description = "根據評論 ID 查詢該評論的所有歷史記錄（創建、編輯等）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得評論歷史",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = reviewHistory.class))),
        @ApiResponse(responseCode = "404", description = "找不到該評論的歷史記錄"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> getReviewHistoriesByReviewId(
            @Parameter(description = "評論 ID", required = true)
            @PathVariable String reviewId) {
        try {
            List<reviewHistory> histories = historyService.getReviewHistoriesByReviewId(reviewId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到評論的歷史記錄",
                    "reviewId", reviewId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/review")
    @Operation(
        summary = "新增評論歷史記錄",
        description = "創建新的評論歷史記錄（userID 從 JWT token 中自動取得）"
    )
    public ResponseEntity<?> createReviewHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateReviewHistoryRequest request) {
        try {
            // 從 JWT token 中提取 username，然後查詢 userID
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
            String userID = user.getId();

            // 建立歷史記錄
            reviewHistory history = new reviewHistory(userID, request.getReviewID(), request.getActionType());
            reviewHistory saved = historyService.saveReviewHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }

    // ===== 綜合搜尋 =====

    @GetMapping("/search/product/{productId}")
    @Operation(
        summary = "搜尋商品相關的所有歷史記錄",
        description = "根據商品 ID 搜尋所有類型的歷史記錄（競標、瀏覽、購買）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得相關歷史記錄",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = History.class))),
        @ApiResponse(responseCode = "404", description = "找不到該商品的相關歷史記錄"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    public ResponseEntity<?> searchAllHistoriesByProductId(
            @Parameter(description = "商品 ID", required = true)
            @PathVariable String productId) {
        try {
            List<History> histories = historyService.searchAllHistoriesByProductId(productId);
            if (histories.isEmpty()) {
                return ResponseEntity.status(404).body(java.util.Map.of(
                    "status", 404,
                    "message", "找不到該商品的相關歷史記錄",
                    "productId", productId
                ));
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of(
                "status", 500,
                "message", "伺服器錯誤",
                "error", e.getMessage()
            ));
        }
    }
}
