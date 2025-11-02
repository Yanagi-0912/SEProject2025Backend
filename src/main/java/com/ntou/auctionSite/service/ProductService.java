package com.ntou.auctionSite.service;
import com.ntou.auctionSite.model.Product;
import com.ntou.auctionSite.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    private final Map<String, Product> productMap = new HashMap<>();

    public List<Product> getAllProduct(){
            return repository.findAll();
    }

    public Product getProductById(String ProductID) {
        return repository.findById(ProductID)
                .orElseThrow(() -> new NoSuchElementException("Product not found with ProductID: " + ProductID));
    }

    public Product createProduct(Product product){//創建商品

        if (product.getProductID() == null || product.getProductID().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        //確保id沒有和資料庫內的productID重複
        else if(repository.findById(product.getProductID()).isPresent()){
            throw new IllegalStateException("Product ID already exists: " + product.getProductID());
        }
        else{
            return repository.save(product);

        }

    }

    public Product editProduct(Product request,String id){//編輯商品
        Product product = getProductById(id);
        if (product.getProductID() == null || product.getProductID().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        /*else if(repository.findByProductID(product.getProductID()).isPresent()){
            throw new IllegalStateException("Product ID already exists: " + product.getProductID());
        }*/
        else {
            product.setSellerID(request.getSellerID());
            product.setProductName(request.getProductName());
            product.setProductDescription(request.getProductDescription());
            product.setProductPrice(request.getProductPrice());
            product.setProductImage(request.getProductImage());
            product.setProductType(request.getProductType());
            product.setProductStock(request.getProductStock());
            product.setProductCategory(request.getProductCategory());
            product.setProductStatus(request.getProductStatus());
            product.setCreatedTime(request.getCreatedTime());
            product.setUpdatedTime(request.getUpdatedTime());
            product.setAuctionEndTime(request.getAuctionEndTime());
            product.setNowHighestBid(request.getNowHighestBid());
            product.setHighestBidderID(request.getHighestBidderID());
            product.setViewCount(request.getViewCount());
            product.setAverageRating(request.getAverageRating());
            product.setReviewCount(request.getReviewCount());
            product.setTotalSales(request.getTotalSales());
            return repository.save(product);
        }
    }

    public Product publishProduct(String id){//上架商品
        Product product = getProductById(id);
        if(product != null){
            product.setProductStatus(Product.ProductStatuses.ACTIVE);
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
}
