package com.ntou.auctionSite.model;

import com.ntou.auctionSite.model.history.*;
import java.util.ArrayList;

public class User {
    private String UserId;      // 使用者ID
    private String UserName;    // 使用者名稱
    private String Password;    // 使用者密碼(經過hash處理)
    private String UserNickname;// 使用者暱稱
    private String Email;       // 使用者Email
    private String Address;     // 使用者地址
    private String PhoneNumber; // 使用者電話號碼
    private float AverageRating;// 使用者平均評分
    private int RatingCount;    // 使用者被評分次數
    private boolean IsBanned;   // 使用者是否被封鎖

    private ArrayList<browseHistory> browseHistoryArrayList;        // 瀏覽過的商品ID或名稱
    private ArrayList<purchaseHistory> purchaseHistoryArrayList;    // 購買過的商品ID或名稱
    private ArrayList<bidHistory> bidHistoryArrayList;              // 出價紀錄（商品ID + 出價金額等）
    private ArrayList<Product> ownedProducts;   // 擁有的商品ID或名稱(賣家)
    private ArrayList<Product> favoriteList;    // 收藏清單


    //建立使用者
    public User() {
    }

    //使用者ID
    public String getUserId() { return UserId; }
    public void setUserId(String userId) { UserId = userId; }
    //使用者名稱
    public String getUserName() { return UserName; }
    public void setUserName(String userName) { UserName = userName; }
    //使用者密碼
    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }
    //使用者暱稱
    public String getUserNickname() { return UserNickname; }
    public void setUserNickname(String userNickname) { UserNickname = userNickname; }
    //使用者Email
    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }
    //使用者地址
    public String getAddress() { return Address; }
    public void setAddress(String address) { Address = address; }
    //使用者電話號碼
    public String getPhoneNumber() { return PhoneNumber; }
    public void setPhoneNumber(String phoneNumber) { PhoneNumber = phoneNumber; }
    //使用者平均評分
    public float getAverageRating() { return AverageRating; }
    public void setAverageRating(float averageRating) { AverageRating = averageRating; }
    //使用者被評分次數
    public int getRatingCount() { return RatingCount; }
    public void setRatingCount(int ratingCount) { RatingCount = ratingCount; }
    //使用者是否被封鎖
    public boolean isBanned() { return IsBanned; }
    public void setBanned(boolean isBanned) { IsBanned = isBanned;}

    //瀏覽過的商品ID或名稱
    public ArrayList<browseHistory> getBrowseHistoryArrayList() { return browseHistoryArrayList; }
    public void setBrowseHistoryArrayList(ArrayList<browseHistory> browseHistoryArrayList) { this.browseHistoryArrayList = browseHistoryArrayList; }
    //購買過的商品ID或名稱
    public ArrayList<purchaseHistory> getPurchaseHistoryArrayList() { return purchaseHistoryArrayList; }
    public void setPurchaseHistoryArrayList(ArrayList<purchaseHistory> purchaseHistoryArrayList) { this.purchaseHistoryArrayList = purchaseHistoryArrayList; }
    //出價紀錄（商品ID + 出價金額等）
    public ArrayList<bidHistory> getBidHistoryArrayList() { return bidHistoryArrayList; }
    public void setBidHistoryArrayList(ArrayList<bidHistory> bidHistoryArrayList) { this.bidHistoryArrayList = bidHistoryArrayList; }
    //擁有的商品ID或名稱(賣家)
    public ArrayList<Product> getOwnedProducts() { return ownedProducts; }
    public void setOwnedProducts(ArrayList<Product> ownedProducts) { this.ownedProducts = ownedProducts; }
    //收藏清單
    public ArrayList<Product> getFavoriteList() { return favoriteList; }
    public void setFavoriteList(ArrayList<Product> favoriteList) { this.favoriteList = favoriteList; }

}
