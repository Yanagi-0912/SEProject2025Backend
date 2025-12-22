package com.ntou.auctionSite.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateBidHistoryRequest {
    @JsonProperty("productID")
    private String productID;

    @JsonProperty("bidAmount")
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
