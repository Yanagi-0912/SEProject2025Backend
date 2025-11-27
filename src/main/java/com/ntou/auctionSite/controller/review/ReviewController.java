package com.ntou.auctionSite.controller.review;


import com.ntou.auctionSite.model.Review;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.ntou.auctionSite.service.product.ProductService;
import com.ntou.auctionSite.service.product.ReviewService;
import java.util.List;

@RestController
@RequestMapping("api/reviews")
@Tag(name = "評論管理", description = "建立與編輯評論與取得評論歷史之API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    //依據錯誤碼統一回應
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Create review successfully!"),
            @ApiResponse(responseCode = "400", description = "Unable to create a review, possibly because you have not purchased or have already reviewed it."),
            @ApiResponse(responseCode = "500", description = "Server error")
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
    public ResponseEntity<?> editReview(@PathVariable String reviewId,
                                        @RequestParam int starCount,
                                        @RequestParam String content,
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
    public ResponseEntity<List<?>> getAllReviewHistory(){
        return ResponseEntity.ok(reviewService.getAllReviewHistory());
    }
}

