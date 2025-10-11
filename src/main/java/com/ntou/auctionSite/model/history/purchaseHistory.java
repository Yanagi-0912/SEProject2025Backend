package com.ntou.auctionSite.model.history;

public class purchaseHistory extends History{
    final private String OrderID;
    public purchaseHistory(String userID, String OrderID) {
        super(userID);
        this.OrderID = OrderID;
    }
    public String getOrderID() {
        return OrderID;
    }
    @Override
    public String toString() {
        return "purchaseHistory [OrderID=" + getOrderID() + ", getTimeStamp()=" + getTimeStamp() + ", getUserID()=" + getUserID() + "]";
    }
}
