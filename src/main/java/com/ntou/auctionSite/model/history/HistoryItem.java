package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryItem {
    private final String productID;
    private final String sellerID;
    private final String productName;
    private final String productCategory;
    private final int productPrice;
    private final int productQuantity;
    private final int totalPrice;

    // 用於 JSON 反序列化和手動創建的建構子
    @JsonCreator
    public HistoryItem(
            @JsonProperty("productID") String productID,
            @JsonProperty("sellerID") String sellerID,
            @JsonProperty("productName") String productName,
            @JsonProperty("productCategory") String productCategory,
            @JsonProperty("productPrice") int productPrice,
            @JsonProperty("productQuantity") int productQuantity,
            @JsonProperty("totalPrice") int totalPrice) {
        this.productID = productID;
        this.sellerID = sellerID;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.totalPrice = totalPrice;
    }
}
