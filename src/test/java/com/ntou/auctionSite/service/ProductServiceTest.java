package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    private void printAllProducts() {
        System.out.println("Get all products:");
        List<Product> productList = productService.getAllProduct();
        for (Product temp : productList) {
            System.out.println("-----------");
            System.out.println("Name: " + temp.getProductName());
            System.out.println("ID: " + temp.getProductID());
            System.out.println("Price: " + temp.getProductPrice());
            System.out.println("Status: " + temp.getProductStatus());
        }
    }

    @Test
    public void testCreateProduct() {//測試新增商品
        Product product = new Product();
        product.setProductID("P105");
        product.setProductName("馬克杯");
        product.setProductPrice(200);

        Product savedProduct = productService.createProduct(product);
        assertNotNull(savedProduct);//檢查savedProduct是否為null
        assertEquals("P105", savedProduct.getProductID());//檢查P105是否等於getID
        assertEquals("馬克杯", savedProduct.getProductName());
        assertEquals(200, savedProduct.getProductPrice());
    }

    @Test
    public void testPublishProduct() {//測試上架商品
        String productId = "P105";
        productService.publishProduct(productId);
        Product published = productService.getProductById(productId);
        assertNotNull(published);
        assertEquals(Product.ProductStatuses.ACTIVE, published.getProductStatus());
    }

    @Test
    public void testRetrieveProductById() {//測試透過ID取得商品
        String productId = "P105";
        Product retrieved = productService.getProductById(productId);
        assertNotNull(retrieved);
        assertEquals(productId, retrieved.getProductID());
        System.out.println("Retrieved Product: " + retrieved.getProductName() + ", Price: " + retrieved.getProductPrice());
    }

    @Test
    public void testGetAllProducts() {//測試取得所有商品
        printAllProducts();
        List<Product> productList = productService.getAllProduct();
        assertNotNull(productList);
        assertTrue(productList.size() > 0);
    }

    @Test
    public void testEditProduct() {//測試編輯商品
        String productId = "P105";
        Product updateRequest = new Product();
        updateRequest.setProductName("新馬克杯");
        updateRequest.setProductPrice(250);

        productService.editProduct(updateRequest, productId);

        Product edited = productService.getProductById(productId);
        assertEquals("新馬克杯", edited.getProductName());
        assertEquals(250, edited.getProductPrice());
    }

    @Test
    public void testWithdrawProduct() {////測試下嫁商品
        String productId = "P105";
        productService.withdrawProduct(productId);
        Product withdrawn = productService.getProductById(productId);
        assertEquals(Product.ProductStatuses.INACTIVE, withdrawn.getProductStatus());
    }

    @Test
    public void testDeleteProduct() {//測試刪除商品
        String productId = "P105";
        productService.deleteProduct(productId);
        Product deleted = productService.getProductById(productId);
        assertNull(deleted);
    }
}
