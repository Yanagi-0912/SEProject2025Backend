package com.ntou.auctionSite.model.history;

import lombok.Getter;

public class bidHistory extends History{
    @Getter
    private HistoryItem historyItem = null;
    @Getter
    final private int bidAmount;
    @Getter
    final private String ProductID;
    public bidHistory(String userID, String productID, int bidAmount) {
        super(userID);
        this.ProductID = productID;
        this.historyItem = new HistoryItem(productID, 1);
        this.bidAmount = bidAmount;
    }
}
