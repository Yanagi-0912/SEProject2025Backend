package com.ntou.auctionSite.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_coupons")
public class UserCoupon {

    @Id
    private String id;                  // 唯一 ID
    private String userId;              // 擁有此優惠券的使用者 ID
    private String couponID;            // 對應 CouponRepository 的模板 ID
    private LocalDateTime getTime;      // 領取時間
    private LocalDateTime expireTime;   // 過期時間
    private int remainingUsage;         // 剩餘可用次數（例如限量使用）
    private boolean used;               // 是否已使用
    private LocalDateTime usedTime;     // 使用時間，如果已使用則有值
    private String orderID;             // 如果已使用，對應使用的訂單

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}
    public String getCouponID() {return couponID;}
    public void setCouponID(String couponID) {this.couponID = couponID;}
    public LocalDateTime getGetTime() {return getTime;}
    public void setGetTime(LocalDateTime getTime) {this.getTime = getTime;}
    public LocalDateTime getExpireTime() {return expireTime;}
    public void setExpireTime(LocalDateTime expireTime) {this.expireTime = expireTime;}
    public int getRemainingUsage() {return remainingUsage;}
    public void setRemainingUsage(int remainingUsage) {this.remainingUsage = remainingUsage;}
    public boolean isUsed() {return used;}
    public void setUsed(boolean used) {this.used = used;}
    public LocalDateTime getUsedTime() {return usedTime;}
    public void setUsedTime(LocalDateTime usedTime) {this.usedTime = usedTime;}
    public String getOrderID() {return orderID;}
    public void setOrderID(String orderID) {this.orderID = orderID;}
}

