package com.ntou.auctionSite.service;
import com.ntou.auctionSite.model.Product;
import com.ntou.auctionSite.model.ProductTypes;
import com.ntou.auctionSite.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    private final Map<String, Product> productMap = new HashMap<>();

    public List<Product> getAllProduct(){
            try{
                List<Product> productList=repository.findAll();
                if(productList.isEmpty()){
                    throw new NoSuchElementException("No product found!");
                }
                return productList;
            }
            catch(Exception e){
            System.err.println("Error fetching products: " + e.getMessage());
            return Collections.emptyList();//回傳一個不可更改的空list
        }
    }

    public Product getProductById(String ProductID) {
        return repository.findById(ProductID)
                .orElseThrow(() -> new NoSuchElementException("Product not found with ProductID: " + ProductID));
    }

    public List<Product> getProductsByPage(int page, int pageSize) {
        List<Product> allProducts = getAllProduct();
        int total = allProducts.size();
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= total) {
            return Collections.emptyList(); // 超過總頁數就回空
        }

        int toIndex = Math.min(fromIndex + pageSize, total); // 不超過總筆數
        return allProducts.subList(fromIndex, toIndex);
    }

    public Product createProduct(Product product){//創建商品
        String randomId;
        do {
            randomId = "PROD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();//先用8位就好
        }
        while (repository.findById(randomId).isPresent());
        Optional<Product> existing = repository.findBySellerIDAndProductName(
                product.getSellerID(), product.getProductName());
        if(existing.isPresent()) {
            throw new IllegalStateException("同一個賣家已經存在同名商品！");
        }
        product.setProductID(randomId);
        product.setCreatedTime(LocalDateTime.now());
        validateProductFields(product);//驗證合法性
        return repository.save(product);
    }

    public Product editProduct(Product request,String id){//編輯商品
        Product product = getProductById(id);
        // 限制只能改自己上架的商品
        if (!product.getSellerID().equals(request.getSellerID())) {
            throw new SecurityException("You are not authorized to edit this product");
        }
        int price=request.getProductPrice();
        validateProductFields(product);
        //通常可以修改的內容
        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setProductImage(request.getProductImage());
        product.setProductType(request.getProductType());
        product.setProductStock(request.getProductStock());
        product.setProductCategory(request.getProductCategory());
        //修改時要注意條件
        if(request.getProductStatus()!=Product.ProductStatuses.BANNED){
            product.setProductStatus(request.getProductStatus());
        }
        if(request.getProductType()==ProductTypes.DIRECT){
            product.setProductPrice(price);
        }
        product.setUpdatedTime(LocalDateTime.now());

        return repository.save(product);
    }

    public Product publishProduct(String id){//上架商品
        Product product = getProductById(id);
        if(product != null){
            product.setProductStatus(Product.ProductStatuses.ACTIVE);
            product.setUpdatedTime(LocalDateTime.now());
            return repository.save(product);
        }
        else{
            throw new NoSuchElementException("Cannot publish. Product not found with id: " + id);
        }
    }

    public Product withdrawProduct(String id){//下架商品
        Product product = getProductById(id);
        if(product != null){
            product.setProductStatus(Product.ProductStatuses.INACTIVE);
            product.setUpdatedTime(LocalDateTime.now());
            return repository.save(product);
        }
        else{
            throw new NoSuchElementException("Cannot publish. Product not found with id: " + id);
        }
    }
    public void deleteProduct(String id) {//刪除商品
        Product product = getProductById(id);
        repository.delete(product);
    }
    private void validateProductFields(Product product) {//驗證商品欄位
        int price=product.getProductPrice();
        String priceStr = String.valueOf(price);//轉成字串，方便後面探段位數
        int digitCount = priceStr.length();
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price< 0 || digitCount>8) {//避免輸入不合法或是過大的金額
            throw new IllegalArgumentException("Product price must be a positive integer!");
        }
        if (product.getProductStock()<0) {
            throw new IllegalArgumentException("Product stock cannot be negative!");
        }
        if (product.getProductType() == null) {
            throw new IllegalArgumentException("Product type must be specified (DIRECT or AUCTION)!");
        }

        if (product.getProductCategory() == null || product.getProductCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be empty!");
        }

        if (product.getProductStatus() == null) {
            throw new IllegalArgumentException("Product status cannot be null!");
        }

        if (product.getProductType() == ProductTypes.AUCTION) {
            if (product.getAuctionEndTime() == null) {
                throw new IllegalArgumentException("Auction end time must be set for auction products!");
            }
            if (product.getAuctionEndTime().isBefore(product.getCreatedTime())) {
                throw new IllegalArgumentException("Auction end time cannot be before creation time!");
            }
        }

        if (product.getAverageRating() < 0 || product.getAverageRating() > 5) {
            throw new IllegalArgumentException("Average rating must be between 0 and 5!");
        }

        if (product.getReviewCount() < 0) {
            throw new IllegalArgumentException("Review count cannot be negative!");
        }
        if (product.getTotalSales() < 0) {
            throw new IllegalArgumentException("Total sales cannot be negative!");
        }
        if (product.getViewCount() < 0) {
            throw new IllegalArgumentException("View count cannot be negative!");
        }

        if (product.getNowHighestBid() < 0) {
            throw new IllegalArgumentException("Now highest bid cannot be negative!");
        }

        if (product.getCreatedTime() == null) {
            throw new IllegalArgumentException("Created time must be set!");
        }
        if (product.getUpdatedTime() != null &&
                product.getUpdatedTime().isBefore(product.getCreatedTime())) {
            throw new IllegalArgumentException("Updated time cannot be before created time!");
        }

        if (product.getSellerID() == null || product.getSellerID().trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be empty!");
        }

    }
}
