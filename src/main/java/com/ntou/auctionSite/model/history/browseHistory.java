package com.ntou.auctionSite.model.history;

import lombok.Getter;

public class browseHistory extends History{
    @Getter
    private HistoryItem historyItem = null;
    @Getter
    final private String ProductID;
    public browseHistory(String userID, String productID) {
        super(userID);
        this.historyItem = new HistoryItem(productID, 1);
        this.ProductID = productID;
    }
}
