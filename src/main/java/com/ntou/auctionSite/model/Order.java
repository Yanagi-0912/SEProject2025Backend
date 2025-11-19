package com.ntou.auctionSite.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order {
    private String OrderID;             //訂單編號
    private String BuyerID;             //買家ID
    private String SellerID;            //賣家ID
    private Cart cart;                  //訂單內容
    private LocalDateTime OrderTime;    //訂單時間
    private ProductTypes OrderType;     //訂單類型
    private OrderStatuses OrderStatus;  //訂單狀態
    public enum OrderStatuses { PENDING, COMPLETED, CANCELLED, REFUNDED } //待處理、已完成、已取消、已退款



    public Order() {
        this.OrderTime = LocalDateTime.now();
        this.OrderStatus = OrderStatuses.PENDING; //預設為待處理
    }

    //訂單編號
    public String getOrderID() { return OrderID; }
    public void setOrderID(String orderID) { OrderID = orderID; }
    //買家ID
    public String getBuyerID() { return BuyerID; }
    public void setBuyerID(String buyerID) { BuyerID = buyerID; }
    //賣家ID
    public String getSellerID() { return SellerID; }
    public void setSellerID(String sellerID) { SellerID = sellerID; }
    //訂單時間
    public LocalDateTime getOrderTime() { return OrderTime; }
    public void setOrderTime(LocalDateTime orderTime) { OrderTime = orderTime; }
    //訂單類型
    public ProductTypes getOrderType() { return OrderType; }
    public void setOrderType(ProductTypes orderType) { OrderType = orderType; }
    //訂單內容
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    //訂單狀態
    public OrderStatuses getOrderStatus() { return OrderStatus; }
    public void setOrderStatus(OrderStatuses orderStatus) { OrderStatus = orderStatus; }
}
