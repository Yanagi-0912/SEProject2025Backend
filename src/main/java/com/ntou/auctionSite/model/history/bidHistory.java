package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class bidHistory extends History{
    @Getter
    private HistoryItem historyItem = null;
    @Getter
    final private int bidAmount;
    @Getter
    final private String ProductID;

    @JsonCreator
    public bidHistory(
            @JsonProperty("userID") String userID,
            @JsonProperty("productID") String productID,
            @JsonProperty("bidAmount") int bidAmount) {
        super(userID);
        this.ProductID = productID;
        this.bidAmount = bidAmount;
    }

    public void setHistoryItem(HistoryItem historyItem) {
        this.historyItem = historyItem;
    }
}
