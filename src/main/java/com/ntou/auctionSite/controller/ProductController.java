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
@RestController
public class ProductController { // 負責處理商品新增、上下架、查看、修改的class
    @Autowired
    private ProductService productService;

    @GetMapping("/products/")
    public ResponseEntity<List<Product>> getAllProduct() {
        try {
            return ResponseEntity.ok(productService.getAllProduct());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/products/add") // 新增商品
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product saved = productService.createProduct(product);
            return ResponseEntity.status(201).body(saved);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/products/edit/{id}") // 修改商品
    public ResponseEntity<Product> editProduct(@RequestBody Product request,@PathVariable String id) {
        try {
            Product update = productService.editProduct(request, id);
            return ResponseEntity.ok(update);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build(); // not found
        }
    }

    @PutMapping("/products/upload/{id}") // 上架商品
    public ResponseEntity<Product> publishProduct(@PathVariable String id) {
        try {
            return ResponseEntity.ok(productService.publishProduct(id));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/products/withdraw/{id}") // 下架商品
    public ResponseEntity<Product> withdrawProduct(@PathVariable String id) {
        try {
            return ResponseEntity.ok(productService.withdrawProduct(id));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/products/delete/{id}")//刪除產品
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
