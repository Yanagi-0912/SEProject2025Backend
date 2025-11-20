package com.ntou.auctionSite.controller;

import com.ntou.auctionSite.model.Product;
import com.ntou.auctionSite.service.BidService;
import com.ntou.auctionSite.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "競標管理", description = "拍賣競標相關 API - 建立拍賣、出價、結束拍賣、建立訂單等功能")
public class BidController {
    @Autowired BidService bidservice;
    @Autowired ProductService productService;

    @PostMapping("api/createAucs/{id}")
    @Operation(
            summary = "建立拍賣商品",
            description = "將商品設定為拍賣模式，需指定起標價和結束時間。時間格式為 ISO 8601 (yyyy-MM-ddTHH:mm:ss)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "拍賣建立成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Auction created successfully! ProductID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "請求參數錯誤（日期格式錯誤、商品不存在、參數不合法等）",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Date format error: Text '2024-13-01T10:00:00' could not be parsed")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> createAuction(//新增拍賣商品
            @Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable String id,//productID
            @Parameter(description = "起標價格", example = "100", required = true)
            @RequestParam (name="price") int basicBidPrice,
            @Parameter(description = "拍賣結束時間 (ISO 8601 格式)", example = "2024-12-31T23:59:59", required = true)
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

    @GetMapping("api/auctions/")
    @Operation(
            summary = "取得所有拍賣中的商品",
            description = "查詢所有正在進行拍賣的商品列表"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得拍賣商品列表",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Found 5 auction products.")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Error fetching auction products: xxx")
                    )
            )
    })
    public ResponseEntity<?>getAllAuctionProduct(){//取得所有拍賣中的商品
        try {
            List<Product> products = bidservice.getAllAuctionProduct();
            return ResponseEntity.ok("Found " + products.size() + " auction products.");
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching auction products: " + e.getMessage());
        }
    }

    @PostMapping("api/bids/{id}")
    @Operation(
            summary = "競標出價",
            description = "對指定商品進行出價，出價金額必須高於當前最高出價"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "出價成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Bid placed successfully!")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "出價失敗（金額過低、拍賣已結束等）",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Bid error: 出價金額必須高於當前最高出價")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> placeBid(
            @Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable("id") String productID,// 商品id
            @Parameter(description = "出價金額", example = "150", required = true)
            @RequestParam("price") int bidPrice,
            @Parameter(description = "出價者ID", example = "U001", required = true)
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

    @PutMapping("api/{id}/terminate")
    @Operation(
            summary = "結束拍賣",
            description = "手動結束指定商品的拍賣，拍賣結束後將無法再出價"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "拍賣結束成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Auction terminated")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "結束拍賣失敗（拍賣不存在、狀態不符等）",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Termination error: 拍賣已結束")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> terminateAuction(
            @Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable String id){
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
    /*這裡我先註解掉
    @PostMapping("api/orders/{productId}")
    @Operation(
            summary = "建立拍賣訂單",
            description = "拍賣結束後，為得標者建立訂單"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "訂單建立成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Order created successfully! OrderID: O001")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "訂單建立失敗（商品狀態不符、無出價者等）",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Order creation failed: product status not valid or no bidder.")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product not found: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?>createOrder(
            @Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable String productId){
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
    }*/
}