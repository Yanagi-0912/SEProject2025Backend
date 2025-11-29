package com.ntou.auctionSite.controller.review;

import com.ntou.auctionSite.model.Review;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.ntou.auctionSite.service.product.ReviewService;
import java.util.List;

@RestController
@RequestMapping("api/reviews")
@Tag(name = "評論管理", description = "建立與編輯評論與取得評論歷史之API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    @Operation(
            summary = "新增評論",
            description = "建立新的商品評論，需確認使用者已購買該商品且未評論過"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "評論資料",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Review.class),
                    examples = @ExampleObject(
                            name = "新增評論範例",
                            value = "{\n" +
                                    "  \"productID\": \"P001\",\n" +
                                    "  \"starCount\": 5,\n" +
                                    "  \"content\": \"商品非常好！\",\n" +
                                    "  \"imgURL\": \"被評論商品的url\"\n" +
                                    "}"
                    )
            )
    )
    //依據錯誤碼回應
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Create review successfully!",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Review.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"reviewID\": \"REV123\",\n" +
                                            "  \"productID\": \"P001\",\n" +
                                            "  \"userID\": \"USER123\",\n" +
                                            "  \"starCount\": 5,\n" +
                                            "  \"content\": \"商品非常好！\",\n" +
                                            "  \"imgURL\": \"被評論商品的url\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Unable to create a review, possibly because you have not purchased or have already reviewed it.",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(mediaType = "text/plain")
            )
    })
    public ResponseEntity<?> createReview(@RequestBody Review review, Authentication authentication){
        try {
            String currentUserId = authentication.getName();//取得目前使用者的id來驗證
            review.setUserID(currentUserId);
            Review saved = reviewService.createReview(review);
            return ResponseEntity.status(201).body(saved);
        }
        catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("edit/{reviewId}")
    @Operation(
            summary = "修改評論",
            description = "依據 reviewId 修改評論內容、星等與圖片（圖片可選）"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "評論更新成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Review.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"reviewID\": \"REV123\",\n" +
                                            "  \"productID\": \"P001\",\n" +
                                            "  \"userID\": \"USER123\",\n" +
                                            "  \"starCount\": 4,\n" +
                                            "  \"content\": \"更新後評論內容\",\n" +
                                            "  \"imgURL\": \"被評論商品的url\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "評論更新失敗", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤", content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<?> editReview(
            @Parameter(description = "評論ID", example = "REV123", required = true)
            @PathVariable String reviewId,
            @Parameter(description = "星等", example = "5", required = true)
            @RequestParam int starCount,
            @Parameter(description = "評論內容", example = "商品非常好！", required = true)
            @RequestParam String content,
            @Parameter(description = "圖片 URL（可選）", example = "被評論商品的url")
            @RequestParam(required = false) String imgURL,
            Authentication authentication){
        try {
            String currentUserId = authentication.getName();
            Review updated = reviewService.editReview(reviewId, currentUserId, starCount, content, imgURL);
            return ResponseEntity.ok(updated);
        }
        catch (IllegalStateException | IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/history")
    @Operation(
            summary = "取得所有評論歷史",
            description = "回傳系統中所有商品的評論紀錄"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得評論歷史",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Review.class),
                            examples = @ExampleObject(
                                    value = "[{\n" +
                                            "  \"reviewID\": \"REV123\",\n" +
                                            "  \"productID\": \"P001\",\n" +
                                            "  \"userID\": \"USER123\",\n" +
                                            "  \"starCount\": 5,\n" +
                                            "  \"content\": \"商品非常好！\",\n" +
                                            "  \"imgURL\": \"被評論商品的url\"\n" +
                                            "}]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤", content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<List<?>> getAllReviewHistory(){
        return ResponseEntity.ok(reviewService.getAllReviewHistory());
    }
}
