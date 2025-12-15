package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.ArrayList;

public class purchaseHistory extends History{
    @Getter
    private ArrayList<HistoryItem> historyItems = new ArrayList<>();
    @Getter
    final private int ProductQuantity;
    @Getter
    final private ArrayList<String> ProductID = new ArrayList<>();

    @JsonCreator
    public purchaseHistory(
            @JsonProperty("userID") String userID,
            @JsonProperty("ProductID") ArrayList<String> ProductID,
            @JsonProperty("productQuantity") int productQuantity) {
        super(userID);
        this.ProductID.addAll(ProductID);
        this.ProductQuantity = productQuantity;
    }

    public void setHistoryItems(ArrayList<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }
}
