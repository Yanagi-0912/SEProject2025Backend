package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceTest {
    public void printAllProductTest(){
        System.out.println("Get all product");
        List<Product> productList= productService.getAllProduct();
        for(Product temp : productList){
            System.out.println("-----------");
            System.out.println(temp.getProductName());
            System.out.println(temp.getProductID());
            System.out.println(temp.getProductPrice());
            System.out.println(temp.getProductStatus());
        }
    }
    @Autowired
    private ProductService productService;

    @Test
    public void testCreateAndRetrieveProduct() {

        Product product = new Product();
        product.setProductID("P100");
        product.setProductName("測試商品2");
        product.setProductPrice(10087);

        // 新增
        productService.createProduct(product);
        
        // 取回
        System.out.println("Get productID:P100 product");
        Product retrieved = productService.getProductById("P100");
        System.out.println(retrieved.getProductPrice());
        System.out.println(retrieved.getProductName());
        //get all
        printAllProductTest();

        //edit
        Product updateRequest = new Product();
        updateRequest.setProductName("change Product 100 hahaha");
        updateRequest.setProductPrice(878);
        System.out.println("Edit product 100:");
        productService.editProduct(updateRequest,"P100");
        Product edited = productService.getProductById("P100");
        System.out.println("\nAfter edit:");
        System.out.println(edited.getProductPrice());
        System.out.println(edited.getProductName());
        //publish product
        System.out.println("Publish product whose productID is P100");
        productService.publishProduct("P100");
        //withdraw product
        System.out.println("Withdraw product whose productID is P101");
        productService.withdrawProduct("P101");
        System.out.println("\nAfter publish and withdraw:");
        printAllProductTest();
        System.out.println("Delete product whose productID is P100");
        productService.deleteProduct("P100");
        System.out.println("\nAfter delete selected product:");
        printAllProductTest();
    }
}
