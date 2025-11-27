package com.ntou.auctionSite.service.product;

import com.ntou.auctionSite.model.Review;
import com.ntou.auctionSite.model.order.Order;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.model.history.reviewHistory;
import com.ntou.auctionSite.repository.HistoryRepository;
import com.ntou.auctionSite.repository.OrderRepository;
import com.ntou.auctionSite.repository.ReviewRepository;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private HistoryRepository historyRepository;
    private List<reviewHistory> reviewHistories = new ArrayList<>();

    //創建評論並確保一個user只能對一個商品頻論一次
    //此外要有購買過該商品才可以評論(還要想)
    public Review createReview(Review review){
        if (!hasBuyed(review.getUserID(), review.getProductID())) {
            throw new IllegalStateException("Only users who have purchased this product can leave a review！");
        }

        List<Review> existing = reviewRepository.findByUserIDAndProductID(review.getUserID(),review.getProductID());
        if(!existing.isEmpty()) {
            throw new IllegalStateException("The same user has already left a review for the same product!");//translate
        }

        String reviewID;
        do {
            reviewID = "REVW" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();//先用8位就好
        }
        while (reviewRepository.findById(reviewID).isPresent());

        //將評論紀錄存下
        review.setReviewID(reviewID);
        review.setCreatedTime(LocalDateTime.now());
        reviewHistory rh=new reviewHistory(review.getUserID(), review.getReviewID(),review.getProductID(),"CREATED");
        reviewHistories.add(rh);
        historyRepository.save(rh);
        return reviewRepository.save(review);
    }
    //編輯評論:只能改內容、星星數、影像url(非強制)
    public Review editReview(String reviewID,String userID,int starCount,String content,String imgURL){
        Review review = reviewRepository.findById(reviewID)
                .orElseThrow(() -> new RuntimeException("Review not found, reviewID: " + reviewID));

        if (!review.getUserID().equals(userID)) {
            throw new IllegalStateException("You can only edit your review!");
        }
        if(starCount>5 || starCount<1){
            throw new IllegalArgumentException("Star count must between 1 and 5!");
        }
        if(imgURL !=null && !imgURL.isEmpty()){
            review.setImgURL(imgURL);
        }
        review.setComment(content);
        review.setStartCount(starCount);
        review.setUpdatedTime(LocalDateTime.now());
        reviewHistory rh=new reviewHistory(userID, review.getReviewID(),review.getProductID(),"EDIT");
        reviewHistories.add(rh);
        historyRepository.save(rh);
        return reviewRepository.save(review);

    }
    //檢查某使用者是否購買過某商品
    boolean hasBuyed(String userID,String productID){
        return orderRepository.findBuyedProduct(userID,Order.OrderStatuses.COMPLETED,productID ).isPresent();
    }
    //更新商家平均星星數 再看要不要

    public List<reviewHistory> getAllReviewHistory(){
        return reviewHistories;
    }
}
