package com.ntou.auctionSite.model.order;

import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.product.ProductTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderID;             //訂單編號
    private String buyerID;             //買家ID
    private String sellerID;            //賣家ID
    private Cart cart;                  //訂單內容
    private LocalDateTime orderTime;    //訂單時間
    private ProductTypes orderType;     //訂單類型
    private OrderStatuses orderStatus;  //訂單狀態
    public enum OrderStatuses { PENDING, COMPLETED, CANCELLED, REFUNDED } //待處理、已完成、已取消、已退款
    private List<OrderItem> orderItems = new ArrayList<>();//回傳訂單詳細資訊用的


    public Order() {
        this.orderTime = LocalDateTime.now();
        this.orderStatus = OrderStatuses.PENDING; //預設為待處理
    }

    //訂單編號
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    //買家ID
    public String getBuyerID() { return buyerID; }
    public void setBuyerID(String buyerID) { this.buyerID = buyerID; }
    //賣家ID
    public String getSellerID() { return sellerID; }
    public void setSellerID(String sellerID) { this.sellerID = sellerID; }
    //訂單時間
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
    //訂單類型
    public ProductTypes getOrderType() { return orderType; }
    public void setOrderType(ProductTypes orderType) { this.orderType = orderType; }
    //訂單內容
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    //訂單狀態
    public OrderStatuses getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatuses orderStatus) { this.orderStatus = orderStatus; }

    public List<OrderItem> getOrderItems() {return orderItems;}
    public void setOrderItems(List<OrderItem> orderItems) {this.orderItems = orderItems;}
}
