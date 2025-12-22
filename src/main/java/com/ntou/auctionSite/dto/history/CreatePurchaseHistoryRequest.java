package com.ntou.auctionSite.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class CreatePurchaseHistoryRequest {
    @JsonProperty("productID")
    private ArrayList<String> productID;

    @JsonProperty("productQuantity")
    private int productQuantity;

    public ArrayList<String> getProductID() {
        return productID;
    }

    public void setProductID(ArrayList<String> productID) {
        this.productID = productID;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
