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

@RestController
public class BidController {
    @Autowired BidService bidservice;
    @Autowired ProductService productService;
    @PostMapping("/createAucs/{id}")
    public ResponseEntity<Product> createAuction(//新增拍賣商品
            @PathVariable String id,
            @RequestParam (name="price") int basicBidPrice,
            @RequestParam (name="time") String endTime
    ){
        try{
            //time要符合yyyy/MM/ddTHH:mm:ss格式
            LocalDateTime auctionEndTime = LocalDateTime.parse(endTime);
            Product auctionProduct=bidservice.createAuction(basicBidPrice,auctionEndTime,id);
            return ResponseEntity.ok(auctionProduct);
        }
        catch (DateTimeParseException e) {//Datetime解析錯誤時的例外處理
            System.err.println("Error parsing date: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        catch(IllegalArgumentException | IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/auctions/")
    public ResponseEntity<List<Product>>getAllAuctionProduct(){//取得所有拍賣中的商品
        try {
            return ResponseEntity.ok(bidservice.getAllAuctionProduct());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
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
            return ResponseEntity.badRequest().body(e.getMessage());
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/orders/{productId}")
    public ResponseEntity<Order>createOrder(@PathVariable String productId){
        try{
            Product auctionProduct=productService.getProductById(productId);
            Order order=bidservice.createOrder(auctionProduct);
            if (order == null) {
                return ResponseEntity.badRequest().build(); // 商品狀態不符或出價者為空
            }
            return ResponseEntity.ok(order);

        }
        catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
