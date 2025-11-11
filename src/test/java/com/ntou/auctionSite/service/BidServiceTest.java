package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
public class BidServiceTest {
    @Autowired
    private BidService bidService;

    @Test
    public void testGetAllAuctionProduct() {
        List<Product> auctionProductList= bidService.getAllAuctionProduct();
        for(Product temp : auctionProductList){
            System.out.println("-----------");
            System.out.println(temp.getProductName());
            System.out.println(temp.getProductID());
            System.out.println(temp.getProductPrice());
            System.out.println(temp.getProductType());
        }
    }
    private String testProductID="P105";
    private void printBidInfo(List<Product> allAuctions){//印出目前最高價和競標者ID,測試用
        for (Product p : allAuctions) {
            if (p.getProductID().equals(testProductID)) {
                System.out.println("NowHighestBid: " + p.getNowHighestBid() + ", HighestBidderID: " + p.getHighestBidderID());
            }
        }
    }
    int seconds=10;
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime endTime=now.plusSeconds(seconds);//10秒後截止
    //將時間格式化
    DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    @Test
    public void testPlaceBid() {//模擬競標流程

        // 1建立拍賣商品
        Product auctionProduct = bidService.createAuction(100,endTime, "P105");
        if (auctionProduct == null) {
            System.out.println("Failed to create auction product");
            return;
        }
        System.out.println("Created auction product: " + auctionProduct.getProductID() + ", price: " + auctionProduct.getProductPrice());
        System.out.println("Bid create time: "+ timeFormatter.format(now) +
                " Bid will terminate at: "+timeFormatter.format(auctionProduct.getAuctionEndTime()));
        List<Product> allAuctions = bidService.getAllAuctionProduct();

        System.out.println("Try to bid higher than the starting bid");// 出價高於目前最高價
        bidService.placeBid(150, testProductID, "buyer002");
        printBidInfo(bidService.getAllAuctionProduct());

        System.out.println("Try to bid higher than the starting bid again");// 再出更高價
        bidService.placeBid(200, testProductID, "buyer003");
        printBidInfo(bidService.getAllAuctionProduct());

        System.out.println("Try to bid lower than the starting bid");// 嘗試出價低於起標價
        try {
            bidService.placeBid(50,testProductID , "buyer001");
        }
        catch (IllegalArgumentException e) {
            System.err.println("Expected exception caught: " + e.getMessage());
        }
        //時間截止，印出最後得標者與價格
        try{
            Thread.sleep(seconds* 1000L);//這裡我先用time sleep模擬,單位毫秒
            bidService.terminateAuction(testProductID);
            printBidInfo(bidService.getAllAuctionProduct());
        }
        catch (InterruptedException e){
            System.err.println("Interrupt");
        }

    }
}
