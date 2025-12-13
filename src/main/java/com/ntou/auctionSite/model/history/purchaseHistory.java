package com.ntou.auctionSite.model.history;

import lombok.Getter;
import java.util.ArrayList;

public class purchaseHistory extends History{
    @Getter
    private ArrayList<HistoryItem> historyItems = new ArrayList<>();
    @Getter
    final private int ProductQuantity;
    @Getter
    final private ArrayList<String> ProductID = new ArrayList<>();
    public purchaseHistory(String userID, ArrayList<String> ProductID, int productQuantity) {
        super(userID);
        this.ProductID.addAll(ProductID);
        this.ProductQuantity = productQuantity;
        for(String id : ProductID){
            this.historyItems.add(new HistoryItem(id, productQuantity));
        }
    }
}
