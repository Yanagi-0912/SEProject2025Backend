package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class browseHistory extends History{
    @Getter
    private HistoryItem historyItem = null;
    @Getter
    final private String ProductID;

    @JsonCreator
    public browseHistory(
            @JsonProperty("userID") String userID,
            @JsonProperty("productID") String productID) {
        super(userID);
        this.ProductID = productID;
    }

    public void setHistoryItem(HistoryItem historyItem) {
        this.historyItem = historyItem;
    }
}
