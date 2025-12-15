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
    @JsonProperty("ProductQuantity")
    final private int ProductQuantity;
    @Getter
    @JsonProperty("ProductID")
    final private ArrayList<String> ProductID = new ArrayList<>();

    // 無參數建構子（供 Jackson 和 Spring Data MongoDB 使用）
    public purchaseHistory() {
        super();
        this.ProductQuantity = 0;
    }

    // 用於 POST 建立（自動產生 historyID）
    public purchaseHistory(
            String userID,
            ArrayList<String> ProductID,
            int productQuantity) {
        super(userID);
        if (ProductID != null) {
            this.ProductID.addAll(ProductID);
        }
        this.ProductQuantity = productQuantity;
    }

    // 用於從 MongoDB 讀取
    @JsonCreator
    public purchaseHistory(
            @JsonProperty(value = "_id") String historyID,
            @JsonProperty("userID") String userID,
            @JsonProperty("timeStamp") java.time.LocalDateTime timeStamp,
            @JsonProperty("ProductID") ArrayList<String> ProductID,
            @JsonProperty(value = "ProductQuantity") @JsonAlias({"productQuantity"}) int productQuantity,
            @JsonProperty(value = "historyItems", required = false) ArrayList<HistoryItem> historyItems) {
        super(historyID, userID, timeStamp);
        if (ProductID != null) {
            this.ProductID.addAll(ProductID);
        }
        this.ProductQuantity = productQuantity;
        if (historyItems != null) {
            this.historyItems.addAll(historyItems);
        }
    }

    public void setHistoryItems(ArrayList<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }
}
