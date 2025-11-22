package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.Product;
import com.ntou.auctionSite.model.Review;
import com.ntou.auctionSite.model.User;
import com.ntou.auctionSite.model.history.reviewHistory;
import com.ntou.auctionSite.repository.ReviewRepository;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    private List<reviewHistory> reviewHistories = new ArrayList<>();

    //創建評論並確保一個user只能對一個商品頻論一次
    //此外要有購買過該商品才可以評論(還要想)
    public Review createReview(Review review){

        String randomId;
        do {
            randomId = "REVW" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();//先用8位就好
        }
        while (reviewRepository.findById(randomId).isPresent());
        List<Review> existing = reviewRepository.findByUserIDAndProductID(review.getUserID(),review.getProductID());
        if(!existing.isEmpty()) {
            throw new IllegalStateException("同一個使用者已經對同一商品發表評論過了！");//translate
        }
        //將評論紀錄存下
        reviewHistories.add(new reviewHistory(review.getUserID(), review.getReviewID(),review.getProductID(),"CREATED"));
        review.setProductID(randomId);
        review.setCreatedTime(LocalDateTime.now());
        return reviewRepository.save(review);
    }
    //編輯評論:只能改內容、星星數、影像url(非強制)
    public Review editReview(String userID,int starCount,String content,String imgURL){
        User user = userRepository.findById(userID)
                .orElseThrow(()->new RuntimeException("user not found，userID: " + userID));
        if(starCount>5 || starCount<1){
            throw new IllegalArgumentException("Star count must between 1 and 5!");
        }

        Review review=new Review();
        if(imgURL !=null && !imgURL.isEmpty()){
            review.setImgURL(imgURL);
        }
        review.setComment(content);
        review.setStartCount(starCount);
        review.setUpdatedTime(LocalDateTime.now());
        reviewHistories.add(new reviewHistory(userID, review.getReviewID(),review.getProductID(),"EDIT"));
        return reviewRepository.save(review);

    }
    //更新商家平均星星數 再看要不要

    public List<reviewHistory> getAllReviewHistory(){
        return reviewHistories;
    }
}
