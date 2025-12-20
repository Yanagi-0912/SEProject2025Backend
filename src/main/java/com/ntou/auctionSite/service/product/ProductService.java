package com.ntou.auctionSite.service.product;
import com.ntou.auctionSite.dto.product.EditProductRequest;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
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
    //默認升序
    public List<Product> getAllProductSorted(String sortBy,String order){
        List<Product> productList=getAllProduct();
        Comparator<Product> comparator;
        switch(sortBy.trim()){
            case "productPrice":
                comparator=Comparator.comparing(Product::getProductPrice);
                break;
            case "productStock":
                comparator=Comparator.comparing(Product::getProductStock);
                break;
            case "createdTime":
                comparator=Comparator.comparing(Product::getCreatedTime);
                break;
            case "updatedTime":
                comparator=Comparator.comparing(Product::getUpdatedTime);
                break;
            case "auctionEndTime":
                comparator=Comparator.comparing(Product::getAuctionEndTime);
                break;
            case "nowHighestBid":
                comparator=Comparator.comparing(Product::getNowHighestBid);
                break;
            case "viewCount":
                comparator=Comparator.comparing(Product::getViewCount);
                break;
            case "averageRating":
                comparator=Comparator.comparing(Product::getAverageRating);
                break;
            case "reviewCount":
                comparator=Comparator.comparing(Product::getReviewCount);
                break;
            case "totalSales":
                comparator=Comparator.comparing(Product::getTotalSales);
                break;
            default:
                comparator=Comparator.comparing(Product::getProductName);
                break;
        }
        if(order.trim().equals("desc")){
            comparator = comparator.reversed();
        }
        productList.sort(comparator);
        return productList;
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

    public Product createProduct(Product product,String currentUserId){//創建商品
        String randomId;
        product.setSellerID(currentUserId);//先設定sellerID再檢查
        trimProductFields(product);
        List<Product> existing = repository.findBySellerIDAndProductName(
                product.getSellerID(), product.getProductName());
        if(!existing.isEmpty()) {
            throw new IllegalStateException("同一個賣家已經存在同名商品！");
        }

        do {
            randomId = "PROD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();//先用8位就好
        }
        while (repository.findById(randomId).isPresent());

        product.setProductID(randomId);
        product.setCreatedTime(LocalDateTime.now());
        product.setUpdatedTime(LocalDateTime.now());
        validateProductFields(product);//驗證合法性
        updateProductStatus(product);
        return repository.save(product);
    }
    public Product editProduct(String productId, EditProductRequest request, String currentUserId) {
        Product product = getProductById(productId);
        trimProductFields(product);
        trimEditRequest(request);
        // 限制只能改自己上架的商品
        if (!product.getSellerID().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to edit this product");
        }
        //僅更新有值的欄位
        if (request.getProductName() != null) {product.setProductName(request.getProductName());}
        if (request.getProductDescription() != null) {product.setProductDescription(request.getProductDescription());}
        if (request.getProductImage() != null) {product.setProductImage(request.getProductImage());}
        if (request.getProductType() != null && request.getProductType()!=ProductTypes.AUCTION) {
            product.setProductType(request.getProductType());
        }
        if (request.getProductStock() != null) {product.setProductStock(request.getProductStock());}
        if (request.getProductPrice() != null ) {product.setProductPrice(request.getProductPrice());}
        if (request.getProductCategory() != null) {product.setProductCategory(request.getProductCategory());}
        if (request.getProductStatus() != null &&
                request.getProductStatus() != Product.ProductStatuses.BANNED) {
            product.setProductStatus(request.getProductStatus());
        }

        // 驗證更新後欄位
        validateProductFields(product);
        product.setUpdatedTime(LocalDateTime.now());
        updateProductStatus(product);
        return repository.save(product);
    }

    public Product publishProduct(String productID,String currentUserId){//上架商品
        Product product = getProductById(productID);
        if (!product.getSellerID().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to edit this product");
        }
        product.setProductStatus(Product.ProductStatuses.ACTIVE);
        product.setUpdatedTime(LocalDateTime.now());
        return repository.save(product);
    }

    public Product withdrawProduct(String productID,String currentUserId){//下架商品
        Product product = getProductById(productID);
        if (!product.getSellerID().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to edit this product");
        }
        product.setProductStatus(Product.ProductStatuses.INACTIVE);
        product.setUpdatedTime(LocalDateTime.now());
        return repository.save(product);
    }
    public void deleteProduct(String productID,String currentUserId) {//刪除商品
        Product product = getProductById(productID);
        if (!product.getSellerID().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to edit this product");
        }
        repository.delete(product);
    }
    private void updateProductStatus(Product product) {//庫存=0時設為SOLD
        if (product.getProductStock() == 0) {
            product.setProductStatus(Product.ProductStatuses.SOLD);
        }
        else if (product.getProductStatus() == Product.ProductStatuses.SOLD) {
            // 如果之前因為庫存為0被設為 INACTIVE，現在有庫存就恢復 ACTIVE
            product.setProductStatus(Product.ProductStatuses.ACTIVE);
        }
    }

    public List<String> getAllCategory(){

        Set<String> categories=new HashSet<String>() ;
        List<Product> productList=getAllProduct();
        for(Product temp:productList){
            if(temp.getProductCategory()!=null && !temp.getProductCategory().isEmpty()){
                if (temp.getProductStatus() == Product.ProductStatuses.ACTIVE) {
                    categories.add(temp.getProductCategory().trim());
                }
            }
        }
        List<String> result = new ArrayList<>(categories);
        Collections.sort(result);
        return result;
    }
    private void trimProductFields(Product p) {//將字串型態欄位去除空白
        if (p.getProductName() != null) p.setProductName(p.getProductName().trim());
        if (p.getProductDescription() != null) p.setProductDescription(p.getProductDescription().trim());
        if (p.getProductImage() != null) p.setProductImage(p.getProductImage().trim());
        if (p.getProductCategory() != null) p.setProductCategory(p.getProductCategory().trim());
        if (p.getSellerID() != null) p.setSellerID(p.getSellerID().trim());
    }

    private void trimEditRequest(EditProductRequest req) {//將字串型態欄位去除空白
        if (req.getProductName() != null) {req.setProductName(req.getProductName().trim());}
        if (req.getProductDescription() != null) {req.setProductDescription(req.getProductDescription().trim());}
        if (req.getProductImage() != null) {req.setProductImage(req.getProductImage().trim());}
        if (req.getProductCategory() != null) {req.setProductCategory(req.getProductCategory().trim());}
    }

    private void validateProductFields(Product product) {//驗證商品欄位
        int price=product.getProductPrice();
        String priceStr = String.valueOf(price);//轉成字串，方便後面探段位數
        int digitCount = priceStr.length();
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        if (price<= 0 || digitCount>8) {//避免輸入不合法或是過大的金額
            throw new IllegalArgumentException("Product price must be a positive integer and can't be zero!");
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
