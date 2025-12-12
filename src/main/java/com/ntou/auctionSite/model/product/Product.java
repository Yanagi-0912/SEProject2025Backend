package com.ntou.auctionSite.model.product;
//日期時間
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Product {
    @Id
    private String productID;               //產品ID
    private String sellerID;                //賣家ID
    private String productName;             //產品名稱
    private String productDescription;      //產品描述
    private int productPrice;               //產品價格
    private String productImage;            //產品圖片URL
    private ProductTypes productType;       //產品類型 (DIRECT, AUCTION)
    private int productStock;               //產品庫存量
    private String productCategory;         //產品類別
    private ProductStatuses productStatus;  //產品狀態
    private LocalDateTime createdTime;      //產品建立時間
    private LocalDateTime updatedTime;      //產品更新時間
    private LocalDateTime auctionEndTime;   //拍賣結束時間 (僅限拍賣產品)
    private int nowHighestBid;              //目前最高出價
    private String highestBidderID;         //目前最高出價者ID
    private int viewCount;                  //產品瀏覽次數
    private double averageRating;           //產品平均評分
    private int reviewCount;                //產品評論數量
    private int totalSales;                 //產品總銷售量
    public enum ProductStatuses { ACTIVE, INACTIVE, SOLD, BANNED,TERMINATE }//有效、無效、已售出、已封鎖、已結束

    public Product() {
    }

    //產品ID
    public String getProductID() { return productID; }
    public void setProductID(String productID) { this.productID = productID; }
    //賣家ID
    public String getSellerID() { return sellerID; }
    public void setSellerID(String sellerID) { this.sellerID = sellerID; }
    //產品名稱
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    //產品描述
    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    //產品價格
    public int getProductPrice() { return productPrice; }
    public void setProductPrice(int productPrice) { this.productPrice = productPrice; }
    //產品圖片URL
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    //產品類型
    public ProductTypes getProductType() { return productType; }
    public void setProductType(ProductTypes productType) { this.productType = productType; }
    //產品庫存量
    public int getProductStock() { return productStock; }
    public void setProductStock(int productStock) { this.productStock = productStock; }
    //產品類別
    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }
    //產品狀態
    public ProductStatuses getProductStatus() { return productStatus; }
    public void setProductStatus(ProductStatuses productStatus) { this.productStatus = productStatus; }
    //產品建立時間
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    //產品更新時間
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
    //拍賣結束時間
    public LocalDateTime getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) { this.auctionEndTime = auctionEndTime; }
    //目前最高出價
    public int getNowHighestBid() { return nowHighestBid; }
    public void setNowHighestBid(int nowHighestBid) { this.nowHighestBid = nowHighestBid; }
    //目前最高出價者ID
    public String getHighestBidderID() { return highestBidderID; }
    public void setHighestBidderID(String highestBidderID) { this.highestBidderID = highestBidderID; }
    //產品瀏覽次數
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    //產品平均評分
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    //產品評論數量
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    //產品總銷售量
    public int getTotalSales() { return totalSales; }
    public void setTotalSales(int totalSales) { this.totalSales = totalSales; }
}

