package com.ntou.auctionSite.controller;

import com.ntou.auctionSite.model.Order;
import com.ntou.auctionSite.model.Product;
import com.ntou.auctionSite.service.BidService;
import com.ntou.auctionSite.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

//@CrossOrigin("https://se-project2025-frontend-8qpw.vercel.app/")
@CrossOrigin("http://localhost:5173")
@RestController
public class BidController {
    @Autowired BidService bidservice;
    @Autowired ProductService productService;
    @PostMapping("/createAucs/{id}")
    public ResponseEntity<?> createAuction(//新增拍賣商品
            @PathVariable String id,//productID
            @RequestParam (name="price") int basicBidPrice,
            @RequestParam (name="time") String endTime
    ){
        try{
            //time要符合yyyy/MM/ddTHH:mm:ss格式
            LocalDateTime auctionEndTime = LocalDateTime.parse(endTime);
            Product auctionProduct=bidservice.createAuction(basicBidPrice,auctionEndTime,id);
            return ResponseEntity.ok("Auction created successfully! ProductID: " + auctionProduct.getProductID());
        }
        catch (DateTimeParseException e) {//Datetime解析錯誤時的例外處理
            System.err.println("Error parsing date: " + e.getMessage());
            return ResponseEntity.badRequest().body("Date format error: " + e.getMessage());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Product error: " + e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Illegal argument: " + e.getMessage());
        }
        catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Illegal state: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }

    }

    @GetMapping("/auctions/")
    public ResponseEntity<?>getAllAuctionProduct(){//取得所有拍賣中的商品
        try {
            List<Product> products = bidservice.getAllAuctionProduct();
            return ResponseEntity.ok("Found " + products.size() + " auction products.");
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching auction products: " + e.getMessage());
        }
    }

    @PostMapping("/bids/{id}")
    public ResponseEntity<?> placeBid(
            @PathVariable("id") String productID,// 商品id
            @RequestParam("price") int bidPrice,
            @RequestParam("bidderId") String bidderID
    ){//競標出價
        try{
            bidservice.placeBid(bidPrice,productID,bidderID);
            return ResponseEntity.ok("Bid placed successfully!");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Bid error: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/terminate")
    public ResponseEntity<?> terminateAuction(@PathVariable String id){
        try{
            bidservice.terminateAuction(id);
            return ResponseEntity.ok("Auction terminated");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Termination error: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/orders/{productId}")
    public ResponseEntity<?>createOrder(@PathVariable String productId){
        try{
            Product auctionProduct=productService.getProductById(productId);
            Order order=bidservice.createOrder(auctionProduct);
            if (order == null) {
                return ResponseEntity.badRequest().body("Order creation failed: product status not valid or no bidder."); // 商品狀態不符或出價者為空
            }
            return ResponseEntity.ok("Order created successfully! OrderID: " + order.getOrderID());

        }
        catch (NoSuchElementException e){
            return ResponseEntity.status(404).body("Product not found: " + productId);
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("Illegal state for order creation: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}
