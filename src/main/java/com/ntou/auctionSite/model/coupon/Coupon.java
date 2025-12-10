package com.ntou.auctionSite.model.coupon;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

public class Coupon {
    @Id
    private String couponID;
    private String couponName;
    private String description;
    private LocalDateTime expireTime;
    private int couponCount;
    private CouponType discountType; //PERCENT,FIXED,FREESHIP
    private Double discountValue; // 折扣值（0.8）或折抵金額（100）
    private Double minPurchaseAmount;//滿多少折多少
    private boolean used;
    private LocalDateTime usedTime;
    private LocalDateTime createdTime;
    private int maxUsage;//最大使用量

    public String getCouponID() { return couponID; }
    public void setCouponID(String couponID) { this.couponID = couponID; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }


    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }

    public int getCouponCount() { return couponCount; }
    public void setCouponCount(int couponCount) { this.couponCount = couponCount; }

    public CouponType getDiscountType() { return discountType; }
    public void setDiscountType(CouponType discountType) { this.discountType = discountType; }

    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }

    public Double getMinPurchaseAmount() { return minPurchaseAmount; }
    public void setMinPurchaseAmount(Double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public LocalDateTime getUsedTime() { return usedTime; }
    public void setUsedTime(LocalDateTime usedTime) { this.usedTime = usedTime; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public int getMaxUsage() { return maxUsage; }
    public void setMaxUsage(int maxUsage) { this.maxUsage = maxUsage; }

}
