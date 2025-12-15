package com.ntou.auctionSite.dto.history;

import java.util.ArrayList;

public class CreatePurchaseHistoryRequest {
    private ArrayList<String> ProductID;
    private int productQuantity;

    public ArrayList<String> getProductID() {
        return ProductID;
    }

    public void setProductID(ArrayList<String> ProductID) {
        this.ProductID = ProductID;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
