package com.ntou.auctionSite.model;
//日期時間
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Product {
    private String ProductID;               //產品ID
    private String SellerID;                //賣家ID
    private String ProductName;             //產品名稱
    private String ProductDescription;      //產品描述
    private int ProductPrice;               //產品價格
    private String ProductImage;            //產品圖片URL
    private ProductTypes ProductType;       //產品類型 (DIRECT, AUCTION)
    private int ProductStock;               //產品庫存量
    private String ProductCategory;         //產品類別
    private ProductStatuses ProductStatus;  //產品狀態
    private LocalDateTime CreatedTime;      //產品建立時間
    private LocalDateTime UpdatedTime;      //產品更新時間
    private LocalDateTime AuctionEndTime;   //拍賣結束時間 (僅限拍賣產品)
    private int NowHighestBid;              //目前最高出價
    private String HighestBidderID;         //目前最高出價者ID
    private int ViewCount;                  //產品瀏覽次數
    private double AverageRating;           //產品平均評分
    private int ReviewCount;                //產品評論數量
    private int TotalSales;                 //產品總銷售量
    enum ProductStatuses { ACTIVE, INACTIVE, SOLD, BANNED }//有效、無效、已售出、已封鎖

    public Product() {
    }

    //產品ID
    public String getProductID() { return ProductID; }
    public void setProductID(String productID) { ProductID = productID; }
    //賣家ID
    public String getSellerID() { return SellerID; }
    public void setSellerID(String sellerID) { SellerID = sellerID; }
    //產品名稱
    public String getProductName() { return ProductName; }
    public void setProductName(String productName) { ProductName = productName; }
    //產品描述
    public String getProductDescription() { return ProductDescription; }
    public void setProductDescription(String productDescription) { ProductDescription = productDescription; }
    //產品價格
    public int getProductPrice() { return ProductPrice; }
    public void setProductPrice(int productPrice) { ProductPrice = productPrice; }
    //產品圖片URL
    public String getProductImage() { return ProductImage; }
    public void setProductImage(String productImage) { ProductImage = productImage; }
    //產品類型
    public ProductTypes getProductType() { return ProductType; }
    public void setProductType(ProductTypes productType) { ProductType = productType; }
    //產品庫存量
    public int getProductStock() { return ProductStock; }
    public void setProductStock(int productStock) { ProductStock = productStock; }
    //產品類別
    public String getProductCategory() { return ProductCategory; }
    public void setProductCategory(String productCategory) { ProductCategory = productCategory; }
    //產品狀態
    public ProductStatuses getProductStatus() { return ProductStatus; }
    public void setProductStatus(ProductStatuses productStatus) { ProductStatus = productStatus; }
    //產品建立時間
    public LocalDateTime getCreatedTime() { return CreatedTime; }
    public void setCreatedTime(LocalDateTime createdTime) { CreatedTime = createdTime; }
    //產品更新時間
    public LocalDateTime getUpdatedTime() { return UpdatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { UpdatedTime = updatedTime; }
    //拍賣結束時間
    public LocalDateTime getAuctionEndTime() { return AuctionEndTime; }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) { AuctionEndTime = auctionEndTime; }
    //目前最高出價
    public int getNowHighestBid() { return NowHighestBid; }
    public void setNowHighestBid(int nowHighestBid) { NowHighestBid = nowHighestBid; }
    //目前最高出價者ID
    public String getHighestBidderID() { return HighestBidderID; }
    public void setHighestBidderID(String highestBidderID) { HighestBidderID = highestBidderID; }
    //產品瀏覽次數
    public int getViewCount() { return ViewCount; }
    public void setViewCount(int viewCount) { ViewCount = viewCount; }
    //產品平均評分
    public double getAverageRating() { return AverageRating; }
    public void setAverageRating(double averageRating) { AverageRating = averageRating; }
    //產品評論數量
    public int getReviewCount() { return ReviewCount; }
    public void setReviewCount(int reviewCount) { ReviewCount = reviewCount; }
    //產品總銷售量
    public int getTotalSales() { return TotalSales; }
    public void setTotalSales(int totalSales) { TotalSales = totalSales; }
}

