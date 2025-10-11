package com.ntou.auctionSite.model.history;

public class browseHistory extends History{
    final private String ProductID;

    public browseHistory(String userID, String productID) {
        super(userID);
        this.ProductID = productID;
    }
    public String getProductID() {
        return ProductID;
    }
    @Override
    public String toString() {
        return "browseHistory [ProductID=" + getProductID() + ", getTimeStamp()=" + getTimeStamp() + ", getUserID()=" + getUserID() + "]";
    }
}
