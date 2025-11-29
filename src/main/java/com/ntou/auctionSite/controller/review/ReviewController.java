package com.ntou.auctionSite.controller.review;

import com.ntou.auctionSite.model.Review;
import com.ntou.auctionSite.service.user.UserService;
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
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "評論管理", description = "建立與編輯評論與取得評論歷史之API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;
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
                                    "  \"productID\": \"PROD5B82D1D6\",\n" +
                                    "  \"starCount\": 5,\n" +
                                    "  \"comment\": \"商品非常好！\",\n" +
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
                                            "  \"productID\": \"PROD5B82D1D6\",\n" +
                                            "  \"userID\": \"USER123\",\n" +
                                            "  \"starCount\": 5,\n" +
                                            "  \"comment\": \"商品非常好！\",\n" +
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
            String username = authentication.getName();
            String currentUserId =userService.getUserInfo(username).id() ;//取得目前使用者的id來驗證
            review.setUserID(currentUserId);
            Review saved = reviewService.createReview(review);
            return ResponseEntity.status(201).body(saved);
        }
        catch (NoSuchElementException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
        catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/edit/{reviewId}")
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
                                            "  \"productID\": \"PROD5B82D1D6\",\n" +
                                            "  \"userID\": \"USER123\",\n" +
                                            "  \"starCount\": 4,\n" +
                                            "  \"comment\": \"更新後評論內容\",\n" +
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
            @RequestParam String comment,
            @Parameter(description = "圖片 URL（可選）", example = "被評論商品的url")
            @RequestParam(required = false) String imgURL,
            Authentication authentication
    ){
        try {
            String username = authentication.getName();
            String currentUserId =userService.getUserInfo(username).id() ;//取得目前使用者的id來驗證
            Review updated = reviewService.editReview(reviewId, currentUserId, starCount, comment, imgURL);
            return ResponseEntity.ok(updated);
        }
        catch (SecurityException e){
            return ResponseEntity.status(403).body(e.getMessage());
        }
        catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    //依商品ID取得評論
    @GetMapping("/byProduct/{productID}")
    @Operation(
            summary = "依商品ID取得評論",
            description = "回傳指定商品的所有評論"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得評論列表",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Review.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "該商品沒有任何評論",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(mediaType = "text/plain")
            )
    })
    public ResponseEntity<List<Review>> getReviewsByProductId(
            @Parameter(description = "商品ID", example = "PROD5B82D1D6", required = true)
            @PathVariable String productID) {
        try {
            List<Review> reviews = reviewService.getReviewByProductId(productID);
            return ResponseEntity.ok(reviews);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(null);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/byUser/{userID}")
    @Operation(
            summary = "依使用者ID取得評論",
            description = "回傳指定使用者的所有評論"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得評論列表",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Review.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "該使用者沒有任何評論",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(mediaType = "text/plain")
            )
    })
    public ResponseEntity<List<Review>> getReviewsByUserId(
            @Parameter(description = "使用者ID", example = "USER123", required = true)
            @PathVariable String userID) {
        try {
            List<Review> reviews = reviewService.getReviewByUserId(userID);
            return ResponseEntity.ok(reviews);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(null);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(null);
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
                                            "  \"productID\": \"PROD5B82D1D6\",\n" +
                                            "  \"userID\": \"USER123\",\n" +
                                            "  \"starCount\": 5,\n" +
                                            "  \"comment\": \"商品非常好！\",\n" +
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
