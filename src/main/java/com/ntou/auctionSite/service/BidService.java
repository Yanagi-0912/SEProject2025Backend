package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.Cart;
import com.ntou.auctionSite.model.Order;
import com.ntou.auctionSite.model.Product;
import com.ntou.auctionSite.model.ProductTypes;
import com.ntou.auctionSite.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//這是關於競標系統的service
@Service
public class BidService {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductService productService;
    DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    public Product createAuction(int basicBidPrice, LocalDateTime auctionEndTime,String productID){//設定起標價 截止時間
        Product auctionProduct=productService.getProductById(productID);
        if (auctionProduct==null) {
            System.out.println("Product not found!");
            return null;
        }
        if(basicBidPrice<=0){
            throw new IllegalArgumentException("BidPrice must greater than 0!!!");
        }
        else{//設定價格 時間等等
            auctionProduct.setNowHighestBid(basicBidPrice);//記得先設定bid price
            auctionProduct.setProductPrice(basicBidPrice);
            auctionProduct.setAuctionEndTime(auctionEndTime);
            auctionProduct.setCreatedTime(LocalDateTime.now());
            auctionProduct.setProductType(ProductTypes.AUCTION);
        }
        return repository.save(auctionProduct);
    }
    public List<Product> getAllAuctionProduct(){//取得所有拍賣中的商品
        return repository.findByProductType(ProductTypes.AUCTION);//ACTIVE應該就是指拍賣中吧
    }
    public void placeBid(int bidPrice,String productID,String bidderID){//買家出價
        Product auctionProduct = productService.getProductById(productID);
        if(auctionProduct.getProductStatus()!=Product.ProductStatuses.ACTIVE &&
           auctionProduct.getProductType()!=ProductTypes.AUCTION
        ){
            System.out.println("Product is not for auction or product is inactive!");
            return;
        }
        if (auctionProduct == null) {
            System.out.println("Product not found!");
            return;
        }
        if(bidPrice<=0){
            System.out.println("BidPrice must greater than 0!!!");
        }
        else if(bidPrice<=auctionProduct.getNowHighestBid()){
            throw new IllegalArgumentException("Bid must be higher than current highest bid");
        }
        else{
            auctionProduct.setNowHighestBid(bidPrice);
            auctionProduct.setProductPrice(bidPrice);
            auctionProduct.setUpdatedTime(LocalDateTime.now());
            auctionProduct.setHighestBidderID(bidderID);
            repository.save(auctionProduct);
            System.out.println("Bid placed successfully!");
        }
    }
    @Scheduled(fixedRate = 5000)//Scheduled用來設定5秒檢查一次
    public void checkAndTerminateAuctions() {
        List<Product> auctionList = repository.findByProductType(ProductTypes.AUCTION);

        LocalDateTime now = LocalDateTime.now();
        for (Product p : auctionList) {
            if (p.getProductStatus() == Product.ProductStatuses.ACTIVE &&
                    p.getAuctionEndTime() != null &&
                    now.isAfter(p.getAuctionEndTime())) {

                terminateAuction(p.getProductID());
            }
        }
    }

    public void terminateAuction(String productID){//結束競拍
        Product auctionProduct = productService.getProductById(productID);
        if (auctionProduct==null) {
            System.out.println("Product not found!");
            return;
        }
        LocalDateTime currentTime=LocalDateTime.now();
        //compareTo比較兩個localdate，>0表示前者比較晚發生
        if(currentTime.compareTo(auctionProduct.getAuctionEndTime()) >0){
            System.out.println("Auction is terminated.Current time: "+timeFormatter.format(currentTime));
            auctionProduct.setProductStatus(Product.ProductStatuses.SOLD);//設定為已售出
            System.out.println("Auction winner is ID:"+auctionProduct.getHighestBidderID());
            repository.save(auctionProduct);
            createOrder(auctionProduct);
        }
    }

    public Order createOrder(Product auctionProduct){//結束後系統要自動建立訂單
        if(auctionProduct.getProductStatus()==Product.ProductStatuses.SOLD &&
                auctionProduct.getHighestBidderID()!=null){
            Order order = new Order();
            order.setOrderID(UUID.randomUUID().toString());//訂單用隨機id
            order.setBuyerID(auctionProduct.getHighestBidderID());
            order.setSellerID(auctionProduct.getSellerID());
            Cart cart = new Cart();
            cart.addProduct(auctionProduct.getProductID());
            order.setCart(cart);
            order.setOrderType(ProductTypes.AUCTION);
            return order;
        }
        return null;
    }
}
