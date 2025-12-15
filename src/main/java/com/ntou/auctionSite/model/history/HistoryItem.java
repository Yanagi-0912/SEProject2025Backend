package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import com.ntou.auctionSite.service.product.ProductService;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;

@Getter@Setter
public class HistoryItem {
    @Getter
    private final String productID;
    @Getter
    private final String sellerID;
    private final String productName;
    private final String productCategory;
    private final int productPrice;
    private final int productQuantity;
    private final int totalPrice;

    // 用於從資料庫查詢產品資訊的建構子
    public HistoryItem(String ProductID, int productQuantity) {
        ProductService productService = new ProductService();
        Product product = productService.getProductById(ProductID);
        this.productID = ProductID;
        this.sellerID = product.getSellerID();
        this.productName = product.getProductName();
        this.productCategory = product.getProductCategory();
        this.productPrice = product.getProductPrice();
        this.productQuantity = productQuantity;
        this.totalPrice = productPrice * productQuantity;
    }

    // 用於 JSON 反序列化的建構子
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
