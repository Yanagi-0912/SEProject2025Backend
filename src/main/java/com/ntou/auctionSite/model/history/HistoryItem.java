package com.ntou.auctionSite.model.history;

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
}
