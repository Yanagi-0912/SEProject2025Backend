package com.ntou.auctionSite.controller.order;

import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.order.Order;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import com.ntou.auctionSite.service.order.OrderService;
import com.ntou.auctionSite.service.product.ProductService;
import com.ntou.auctionSite.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "訂單管理", description = "建立與取得訂單之API")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    //建立訂單
    @Operation(
            summary = "建立訂單",
            description = "",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Order.class),
                            examples = {
                                    @ExampleObject(
                                            name = "直購訂單範例",
                                            value = "{\n" +
                                                    "  \"orderType\": \"DIRECT\",\n" +
                                                    "  \"buyerID\": \"69256cd86d01b3ac2b6f1704\",\n" +
                                                    "  \"cart\": {\n" +
                                                    "    \"items\": [\n" +
                                                    "      { \"productId\": \"PROD5B82D1D6\", \"quantity\": 1 }\n" +
                                                    "    ]\n" +
                                                    "  }\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "拍賣訂單範例",
                                            value = "{\n" +
                                                    "  \"orderType\": \"AUCTION\",\n" +
                                                    "  \"cart\": {\n" +
                                                    "    \"items\": [\n" +
                                                    "      { \"productId\": \"PRODBEF68E15\", \"quantity\": 1 }\n" +
                                                    "    ]\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            }
                    )
            )
    )

    @PostMapping("/add")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "訂單建立成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Order created successfully! OrderID: ORDas1c5ada16da")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "請求內容錯誤或商品庫存不足",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Error creating order: 商品庫存不足以應付訂單要求數量")
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "沒有權限建立訂單",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "You are not authorized to create this order")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product not found with ID: P001")
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
    public ResponseEntity<?> createOrder(@org.springframework.web.bind.annotation.RequestBody Order order,
                                         Authentication authentication) {
        try {
            String username=authentication.getName();
            String currentUserId = userService.getUserInfo(username).id();
            //驗證購買者和目前user是否相同，避免他人亂下訂單
            if (order.getBuyerID() != null && !order.getBuyerID().equals(currentUserId)) {
                return ResponseEntity.status(403)
                        .body("You are not allowed to create an order for another user!");
            }
            if (order.getOrderType() == ProductTypes.AUCTION) {
                Cart.CartItem item = order.getCart().getItems().get(0);
                if (item == null) {
                    return ResponseEntity.badRequest().body("No Auction product in cart!");
                }
                Product auctionProduct = productService.getProductById(item.getProductId());
                if (!auctionProduct.getHighestBidderID().equals(currentUserId)) {
                    return ResponseEntity.status(403)
                            .body("You are not the highest bidder, cannot create this auction order.");
                }
                Order createdOrder = orderService.createOrder(
                        order,
                        auctionProduct.getHighestBidderID(),
                        ProductTypes.AUCTION
                );
                return ResponseEntity.ok("Auction order created successfully! OrderID: " + createdOrder.getOrderID());
            }
            else {// 直購商品
                if (order.getCart() == null || order.getCart().getItems().isEmpty()) {
                    return ResponseEntity.badRequest().body("Cart is empty!");
                }

                Order createdOrder = orderService.createOrder(
                        order,
                        currentUserId,
                        ProductTypes.DIRECT
                );
                return ResponseEntity.ok("Order created successfully! OrderID: " + createdOrder.getOrderID());
            }
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found: " + e.getMessage());
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("Illegal state for order creation: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @Operation(summary = "依照訂單ID結帳，並可套用優惠券")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "訂單付款成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Order paid successfully! OrderID: ORDE2003F99-2"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "訂單狀態不允許付款",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Order cannot be paid because it is not in PENDING status!"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "沒有權限付款該訂單",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "You are not allowed to pay this order for another user!"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到訂單",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Order not found with ID: ORDE2003F99-2"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Server error: xxx"
                            )
                    )
            )
    })
    @PutMapping("/pay/{orderID}")
    public ResponseEntity<?> payOrder(
            @Parameter(description = "訂單ID", example = "ORDE2003F99-2", required = true)
            @PathVariable String orderID,
            @Parameter(description = "優惠券ID（可選）", example = "COUP7EC9E12A", required = false)
            @RequestParam(required = false) String couponID,
            Authentication authentication
    ){
        try {
            Order order = orderService.getOrderById(orderID.trim());
            String username = authentication.getName();
            String currentUserId = userService.getUserInfo(username).id();

            // 驗證購買者身份
            if (order.getBuyerID() != null && !order.getBuyerID().equals(currentUserId)) {
                return ResponseEntity.status(403)
                        .body("You are not allowed to pay this order for another user!");
            }

            // 呼叫 Service 結帳，傳入 couponID
            Order paidOrder = orderService.payOrder(orderID, couponID);

            return ResponseEntity.ok("Order paid successfully! OrderID: " + paidOrder.getOrderID());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }


    //找訂單
    @Operation(
            summary = "依照訂單ID搜尋",
            description = "",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "成功取得訂單",
                            content = @Content(
                                    schema = @Schema(implementation = Order.class),
                                    examples = @ExampleObject(
                                            name = "查詢成功範例",
                                            value = "{\n" +
                                                    "  \"orderType\": \"AUCTION\",\n" +
                                                    "  \"cart\": {\n" +
                                                    "    \"items\": [\n" +
                                                    "      { \"productId\": \"PRODBEF68E15\", \"quantity\": 1 }\n" +
                                                    "    ]\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{orderID}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得訂單資訊",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Order found successfully! OrderID: ORDE2003F99-2"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "沒有權限查詢該訂單",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "You are not allowed to view this order!"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到訂單",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Order not found with ID: ORDE2003F99-2"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Server error: xxx"
                            )
                    )
            )
    })

    public ResponseEntity<?> getOrderById(
            @Parameter(description = "訂單ID", example = "ORDE2003F99-2", required = true)
            @PathVariable String orderID

    ){
        try {
            return ResponseEntity.ok(orderService.getOrderById(orderID.trim()));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Order not found with ID: " + orderID);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "依照買家ID搜尋訂單",
            description = "查詢指定買家（buyerID）所建立的所有訂單"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得買家訂單列表",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class),
                            examples = @ExampleObject(
                                    name = "查詢成功範例",
                                    value = "[\n" +
                                            "  {\n" +
                                            "    \"orderID\": \"ORDE2003F99-1\",\n" +
                                            "    \"buyerID\": \"692457c8cdb0278a527e86ab\",\n" +
                                            "    \"orderType\": \"DIRECT\",\n" +
                                            "    \"status\": \"COMPLETED\"\n" +
                                            "  },\n" +
                                            "  {\n" +
                                            "    \"orderID\": \"ORDE2003F99-2\",\n" +
                                            "    \"buyerID\": \"692457c8cdb0278a527e86ab\",\n" +
                                            "    \"orderType\": \"AUCTION\",\n" +
                                            "    \"status\": \"PENDING\"\n" +
                                            "  }\n" +
                                            "]"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到該買家的訂單",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Order not found with buyerID: 692457c8cdb0278a527e86ab"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Server error: xxx"
                            )
                    )
            )
    })
    @GetMapping("/buyer")
    public ResponseEntity<?> getOrderByBuyerId(
            @Parameter(
                    description = "買家ID",
                    example = "692457c8cdb0278a527e86ab",
                    required = true
            )
            @RequestParam String buyerId
    ) {
        try {
            return ResponseEntity.ok(orderService.getOrderByBuyerId(buyerId));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404)
                    .body("Order not found with buyerID: " + buyerId);
        }
        catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Server error: " + e.getMessage());
        }
    }

}
