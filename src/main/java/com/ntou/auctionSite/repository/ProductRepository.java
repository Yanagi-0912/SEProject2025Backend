package com.ntou.auctionSite.repository;
import com.ntou.auctionSite.model.Product;
import com.ntou.auctionSite.model.ProductTypes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByProductType(ProductTypes types);//查找是拍賣類還是直購類
}

