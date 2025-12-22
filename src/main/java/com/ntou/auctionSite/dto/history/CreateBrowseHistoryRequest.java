package com.ntou.auctionSite.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateBrowseHistoryRequest {
    @JsonProperty("productID")
    private String productID;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
