package com.ntou.auctionSite.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Review {
    private String userName;
    private String userID;
    private String reviewID;
    private String productID;
    private String comment;//評論內容
    private LocalDateTime createdTime;//建立評論的時間
    private LocalDateTime updatedTime;//修改評論的時間
    private String imgURL;//
    int startCount;//1-5顆星
    //建立評論
    public Review() {
    }

    public String getUserName(){return userName;}
    public void setUserName(String userName){this.userName=userName;}//有related prob

    public String getUserID(){return userID;}
    public void setUserID(String userID){this.userID= userID;}

    public String getReviewID(){return reviewID;};
    public void setReviewID(String reviewID){this.reviewID= reviewID;}

    public String getProductID(){return productID;};
    public void setProductID(String productID){this.productID= productID;}


    public String getComment(){return comment;};
    public void setComment(String comment){this.comment= comment;}

    public LocalDateTime getCreatedTime(){return createdTime;};
    public void setCreatedTime(LocalDateTime createdTime){this.createdTime= createdTime;}

    public LocalDateTime getUpdatedTime(){return updatedTime;};
    public void setUpdatedTime(LocalDateTime updatedTime){this.updatedTime= updatedTime;}

    public String getImgURL(){return imgURL;};
    public void setImgURL(String imgURL){this.imgURL= imgURL;}

    public int getStartCount(){return startCount;};
    public void setStartCount(int startCount){this.startCount= startCount;}
}
