package com.ntou.auctionSite.model.order;

//回傳訂單詳細資訊用的
public class OrderItem {
    private String productID;    // 商品ID
    private int quantity;        // 購買數量
    private String sellerID;     // 賣家ID
    private double price;        // 商品單價

    public OrderItem() {
    }

    public OrderItem(String productID, int quantity, String sellerID, double price) {
        this.productID = productID;
        this.quantity = quantity;
        this.sellerID = sellerID;
        this.price = price;
    }

    // Getter 和 Setter
    public String getProductID() {return productID;}
    public void setProductID(String productID) {this.productID = productID;}

    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

    public String getSellerID() {return sellerID;}
    public void setSellerID(String sellerID) {this.sellerID = sellerID;}

    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}


    // 計算該商品總價
    public double getTotalPrice() {return this.price * this.quantity;}
}
