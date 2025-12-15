package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class browseHistory extends History{
    @Getter
    @JsonProperty("historyItem")
    private HistoryItem historyItem = null;
    @Getter
    @JsonProperty("ProductID")
    final private String ProductID;

    // 無參數建構子（供 Jackson 和 Spring Data MongoDB 使用）
    public browseHistory() {
        super();
        this.ProductID = null;
    }

    // 用於 POST 建立（自動產生 historyID）
    public browseHistory(String userID, String productID) {
        super(userID);
        this.ProductID = productID;
    }

    // 用於從 MongoDB 讀取
    @JsonCreator
    public browseHistory(
            @JsonProperty(value = "_id") String historyID,
            @JsonProperty("userID") String userID,
            @JsonProperty("timeStamp") java.time.LocalDateTime timeStamp,
            @JsonProperty("ProductID") @JsonAlias({"productID"}) String productID,
            @JsonProperty(value = "historyItem", required = false) HistoryItem historyItem) {
        super(historyID, userID, timeStamp);
        this.ProductID = productID;
        this.historyItem = historyItem;
    }

    public void setHistoryItem(HistoryItem historyItem) {
        this.historyItem = historyItem;
    }
}
