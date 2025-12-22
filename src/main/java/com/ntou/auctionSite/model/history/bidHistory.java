package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class bidHistory extends History{
    @Getter
    @JsonProperty("historyItem")
    private HistoryItem historyItem = null;

    @Getter
    @JsonProperty("bidAmount")
    private final int bidAmount;

    @Getter
    @JsonProperty("productID")
    private final String productID;

    // 無參數建構子（供 Jackson 和 Spring Data MongoDB 使用）
    public bidHistory() {
        super();
        this.productID = null;
        this.bidAmount = 0;
    }

    // 用於 POST 建立（自動產生 historyID）
    public bidHistory(
            String userID,
            String productID,
            int bidAmount) {
        super(userID);
        this.productID = productID;
        this.bidAmount = bidAmount;
    }

    // 用於從 MongoDB 讀取
    @JsonCreator
    public bidHistory(
            @JsonProperty(value = "_id") String historyID,
            @JsonProperty("userID") String userID,
            @JsonProperty("timeStamp") java.time.LocalDateTime timeStamp,
            @JsonProperty("productID") @JsonAlias({"ProductID"}) String productID,
            @JsonProperty("bidAmount") int bidAmount,
            @JsonProperty(value = "historyItem", required = false) HistoryItem historyItem) {
        super(historyID, userID, timeStamp);
        this.productID = productID;
        this.bidAmount = bidAmount;
        this.historyItem = historyItem;
    }

    public void setHistoryItem(HistoryItem historyItem) {
        this.historyItem = historyItem;
    }
}
