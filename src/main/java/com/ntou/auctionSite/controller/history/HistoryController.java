package com.ntou.auctionSite.controller.history;

import com.ntou.auctionSite.model.history.*;
import com.ntou.auctionSite.service.history.HistoryService;
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
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/history")
@Tag(name = "歷史記錄管理", description = "使用者歷史記錄相關 API - 包含瀏覽、競標、購買、評論歷史")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

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
                return ResponseEntity.status(404).body("找不到使用者 " + userId + " 的歷史記錄");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
        }
    }

    @GetMapping("/{historyId}")
    @Operation(
        summary = "根據歷史記錄 ID 查詢",
        description = "根據歷史記錄 ID 查詢特定的歷史記錄"
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
            return historyService.getHistoryById(historyId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(404).body(null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到使用者 " + userId + " 的競標歷史");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到商品 " + productId + " 的競標歷史");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/bid")
    @Operation(
        summary = "新增競標歷史記錄",
        description = "創建新的競標歷史記錄"
    )
    public ResponseEntity<?> createBidHistory(@RequestBody bidHistory history) {
        try {
            bidHistory saved = historyService.saveBidHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到使用者 " + userId + " 的瀏覽歷史");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到商品 " + productId + " 的瀏覽歷史");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/browse")
    @Operation(
        summary = "新增瀏覽歷史記錄",
        description = "創建新的瀏覽歷史記錄"
    )
    public ResponseEntity<?> createBrowseHistory(@RequestBody browseHistory history) {
        try {
            browseHistory saved = historyService.saveBrowseHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到使用者 " + userId + " 的購買歷史");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到商品 " + productId + " 的購買歷史");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/purchase")
    @Operation(
        summary = "新增購買歷史記錄",
        description = "創建新的購買歷史記錄"
    )
    public ResponseEntity<?> createPurchaseHistory(@RequestBody purchaseHistory history) {
        try {
            purchaseHistory saved = historyService.savePurchaseHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到使用者 " + userId + " 的評論歷史");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到評論 " + reviewId + " 的歷史記錄");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
        }
    }

    @PostMapping("/review")
    @Operation(
        summary = "新增評論歷史記錄",
        description = "創建新的評論歷史記錄"
    )
    public ResponseEntity<?> createReviewHistory(@RequestBody reviewHistory history) {
        try {
            reviewHistory saved = historyService.saveReviewHistory(history);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
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
                return ResponseEntity.status(404).body("找不到商品 " + productId + " 的相關歷史記錄");
            }
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤: " + e.getMessage());
        }
    }
}
