package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.ArrayList;

public class purchaseHistory extends History{
    @Getter
    @JsonProperty("historyItems")
    private ArrayList<HistoryItem> historyItems = new ArrayList<>();

    @Getter
    @JsonProperty("productQuantity")
    private final int productQuantity;

    @Getter
    @JsonProperty("productID")
    private final ArrayList<String> productID = new ArrayList<>();

    // 無參數建構子（供 Jackson 和 Spring Data MongoDB 使用）
    public purchaseHistory() {
        super();
        this.productQuantity = 0;
    }

    // 用於 POST 建立（自動產生 historyID）
    public purchaseHistory(
            String userID,
            ArrayList<String> productID,
            int productQuantity) {
        super(userID);
        if (productID != null) {
            this.productID.addAll(productID);
        }
        this.productQuantity = productQuantity;
    }

    // 用於從 MongoDB 讀取
    @JsonCreator
    public purchaseHistory(
            @JsonProperty(value = "_id") String historyID,
            @JsonProperty("userID") String userID,
            @JsonProperty("timeStamp") java.time.LocalDateTime timeStamp,
            @JsonProperty("productID") @JsonAlias({"ProductID"}) ArrayList<String> productID,
            @JsonProperty("productQuantity") @JsonAlias({"ProductQuantity"}) int productQuantity,
            @JsonProperty(value = "historyItems", required = false) ArrayList<HistoryItem> historyItems) {
        super(historyID, userID, timeStamp);
        if (productID != null) {
            this.productID.addAll(productID);
        }
        this.productQuantity = productQuantity;
        if (historyItems != null) {
            this.historyItems.addAll(historyItems);
        }
    }

    public void setHistoryItems(ArrayList<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }
}
