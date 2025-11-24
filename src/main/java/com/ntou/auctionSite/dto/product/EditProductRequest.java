package com.ntou.auctionSite.dto.product;

import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;

public class EditProductRequest {
    private String productName;
    private String productDescription;
    private String productImage;
    private ProductTypes productType;
    private Integer productStock;      // 注意 Integer，可為 null
    private Integer productPrice;      // 注意 Integer，可為 null
    private String productCategory;
    private Product.ProductStatuses productStatus;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public ProductTypes getProductType() { return productType; }
    public void setProductType(ProductTypes productType) { this.productType = productType; }

    public Integer getProductStock() { return productStock; }
    public void setProductStock(Integer productStock) { this.productStock = productStock; }

    public Integer getProductPrice() { return productPrice; }
    public void setProductPrice(Integer productPrice) { this.productPrice = productPrice; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public Product.ProductStatuses getProductStatus() { return productStatus; }
    public void setProductStatus(Product.ProductStatuses productStatus) { this.productStatus = productStatus; }
}
