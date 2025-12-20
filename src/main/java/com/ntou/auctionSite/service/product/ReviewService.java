package com.ntou.auctionSite.service.product;

import com.ntou.auctionSite.model.Review;
import com.ntou.auctionSite.model.order.Order;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.history.reviewHistory;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.*;
import com.ntou.auctionSite.repository.history.HistoryRepository;
import com.ntou.auctionSite.repository.history.ReviewHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReviewHistoryRepository reviewHistoryRepository;
    private List<reviewHistory> reviewHistories = new ArrayList<>();
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    //創建評論並確保一個user只能對一個商品頻論一次
    //此外要有購買過該商品才可以評論
    public Review createReview(Review review,String username){
        review.setReviewID(null);
        int starCount= review.getStarCount();
        if (!hasBuyed(review.getUserID(), review.getProductID())) {
            throw new IllegalStateException("Only users who have purchased this product can leave a review！");
        }
        List<Review> existing = reviewRepository.findByUserIDAndProductID(review.getUserID(),review.getProductID());
        if(!existing.isEmpty()) {
            throw new IllegalStateException("The same user has already left a review for the same product!");//translate
        }
        if(starCount > 5 || starCount < 1){
            throw new IllegalArgumentException("Star count must be between 1 and 5!");
        }
        String reviewID;
        do {
            reviewID = "REVW" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();//先用8位就好
        }
        while (reviewRepository.findById(reviewID).isPresent());
        Product product=productService.getProductById(review.getProductID());
        int reviewCount=product.getReviewCount();
        String sellerId=product.getSellerID();
        double averageRating=product.getAverageRating();
        double newTotal = averageRating * reviewCount + starCount;
        double newAvgRating = newTotal / (reviewCount + 1);

        //更新商品平均星數、評論數
        product.setReviewCount(reviewCount+1);
        product.setAverageRating(newAvgRating);
        productRepository.save(product);
        //更新賣家平均星數
        User seller=userRepository.findById(sellerId)
                .orElseThrow(() -> new NoSuchElementException("Seller not found with id: " + sellerId));
        float oldSellerTotal = seller.getAverageRating() * (seller.getRatingCount() != null ? seller.getRatingCount() : 0);
        float newSellerTotal = oldSellerTotal + starCount;
        seller.setRatingCount((seller.getRatingCount() != null ? seller.getRatingCount() : 0) + 1);
        seller.setAverageRating(newSellerTotal / seller.getRatingCount());

        userRepository.save(seller);
        //將評論紀錄存下
        review.setReviewID(reviewID);
        review.setUserName(username);
        review.setCreatedTime(LocalDateTime.now());
        review.setStarCount(starCount);
        review.setComment(review.getComment());
        reviewHistory rh=new reviewHistory(review.getUserID(), review.getReviewID(),"CREATED");
        reviewHistories.add(rh);
        reviewHistoryRepository.save(rh);
        return reviewRepository.save(review);
    }
    //編輯評論:只能改內容、星星數、影像url(非強制)
    public Review editReview(String reviewID, String userID, int starCount, String content, String imgURL){
        Review review = reviewRepository.findById(reviewID)
                .orElseThrow(() -> new NoSuchElementException("Review not found, reviewID: " + reviewID));

        if (!review.getUserID().trim().equalsIgnoreCase(userID.trim())) {
            throw new SecurityException("You can only edit your review!");
        }

        if(starCount > 5 || starCount < 1){
            throw new IllegalArgumentException("Star count must be between 1 and 5!");
        }

        if(imgURL != null && !imgURL.isEmpty()){
            review.setImgURL(imgURL);
        }
        Product product=productService.getProductById(review.getProductID());
        int reviewCount=product.getReviewCount();
        int oldStar=review.getStarCount();
        String sellerId= product.getSellerID();
        double averageRating=product.getAverageRating();
        double total=averageRating*reviewCount;
        //記得扣掉舊的星數
        double newTotal = total-review.getStarCount()+starCount;
        double newAvgRating=newTotal/reviewCount;
        //更新商品平均星數
        product.setAverageRating(newAvgRating);
        review.setComment(content);
        review.setStarCount(starCount);
        review.setUpdatedTime(LocalDateTime.now());
        productRepository.save(product);
        //更新賣家平均星數
        User seller=userRepository.findById(sellerId)
                .orElseThrow(() -> new NoSuchElementException("Seller not found with id: " + sellerId));
        float oldSellerTotal = seller.getAverageRating() * seller.getRatingCount();
        float newSellerTotal = oldSellerTotal - oldStar + starCount;
        seller.setAverageRating(newSellerTotal / seller.getRatingCount());

        userRepository.save(seller);
        reviewHistory rh = new reviewHistory(userID, review.getReviewID(), "EDIT");
        reviewHistories.add(rh);
        reviewHistoryRepository.save(rh);
        return reviewRepository.save(review);
    }

    //檢查某使用者是否購買過某商品
    boolean hasBuyed(String userID,String productID){
        List<Order> orderList=orderRepository.findBuyedProduct(userID,Order.OrderStatuses.COMPLETED,productID);
        if(!orderList.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }

    public List<Review> getReviewByProductId(String productID){
        List<Review> reviewList=reviewRepository.findByProductID(productID);
        if(reviewList.isEmpty()){
            throw new NoSuchElementException("Review not found with productID: " + productID);
        }
        return reviewList;
    }

    public List<Review> getReviewByUserId(String userID){
        List<Review> reviewList=reviewRepository.findByUserID(userID);
        if(reviewList.isEmpty()){
            throw new NoSuchElementException("Review not found with userID: " + userID);
        }
        return reviewList;
    }

}
