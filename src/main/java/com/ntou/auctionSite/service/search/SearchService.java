package com.ntou.auctionSite.service.search;

import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

//提供根據關鍵字搜尋、模糊搜尋等功能
@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final ProductRepository productRepository;
    public List<Product> searchByKeyword(String keyword){//精確搜尋
        try{
            List<Product> productList=productRepository.findByProductName(keyword);
            if(productList.isEmpty()){
                throw new NoSuchElementException("No product found!");
            }
            return productList;
        }
        catch(Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
            return Collections.emptyList();//回傳一個不可更改的空list
        }
    }
    public List<Product> blurSearch(String keyword){
        try{
            List<Product> productList=productRepository.searchProducts(keyword);
            if(productList.isEmpty()){
                throw new NoSuchElementException("No product found!");
            }
            return productList;
        }
        catch(Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
            return Collections.emptyList();//回傳一個不可更改的空list
        }
    }
}
