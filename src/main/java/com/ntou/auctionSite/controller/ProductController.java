package com.ntou.auctionSite.controller;

import java.util.*;

import com.ntou.auctionSite.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ntou.auctionSite.service.ProductService;
//open postman and select POST
// http://localhost:8080/products/add
// {
//      "productID": "P001",
//      "sellerID": "S001",
//      "productName": "餅乾",
//      "productPrice": 100,
//      "productType": "DIRECT"
// }
@CrossOrigin("http://localhost:5173")
@RestController
public class ProductController { // 負責處理商品新增、上下架、查看、修改的class
    @Autowired
    private ProductService productService;

    //<?>表示可以是任何型態,前端可以提供第幾頁、每頁大小
    @GetMapping("/products/")
    public ResponseEntity<?> getAllProduct(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Product> products = productService.getProductsByPage(page, pageSize);
            return ResponseEntity.ok(products);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching products: " + e.getMessage());
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/products/add") // 新增商品
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product saved = productService.createProduct(product);
            return ResponseEntity.status(201).body("Product created successfully! ProductID: " + saved.getProductID());
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("Error creating product: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/products/edit/{id}") // 修改商品
    public ResponseEntity<?> editProduct(@RequestBody Product request,@PathVariable String id) {
        try {
            Product update = productService.editProduct(request, id);
            return ResponseEntity.ok("Product updated successfully! ProductID: " + update.getProductID());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id); // not found
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/products/upload/{id}") // 上架商品
    public ResponseEntity<?> publishProduct(@PathVariable String id) {
        try {
            Product published = productService.publishProduct(id);
            return ResponseEntity.ok("Product published successfully! ProductID: " + published.getProductID());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/products/withdraw/{id}") // 下架商品
    public ResponseEntity<?> withdrawProduct(@PathVariable String id) {
        try {
            Product withdrawn = productService.withdrawProduct(id);
            return ResponseEntity.ok("Product withdrawn successfully! ProductID: " + withdrawn.getProductID());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
    @DeleteMapping("/products/delete/{id}")//刪除產品
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully! ProductID: " + id);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}
