package com.ntou.auctionSite.dto.history;

public class CreateBidHistoryRequest {
    private String productID;
    private int bidAmount;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }
}
