package com.ntou.auctionSite.model.history;

public class bidHistory extends History{
    final private String productID;
    final private int bidAmount;
    public bidHistory(String userID, String productID, int bidAmount) {
        super(userID);
        this.productID = productID;
        this.bidAmount = bidAmount;
    }
    public String getProductID() {
        return productID;
    }
    public double getBidAmount() {
        return bidAmount;
    }
    @Override
    public String toString() {
        return "bidHistory [productID=" + productID + ", bidAmount=" + bidAmount + ", getTimeStamp()=" + getTimeStamp()
                + ", getUserID()=" + getUserID() + "]";
    }
}
