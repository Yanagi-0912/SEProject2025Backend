package com.ntou.auctionSite.controller.order;

import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.order.Order;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import com.ntou.auctionSite.service.order.OrderService;
import com.ntou.auctionSite.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@Tag(name = "訂單管理", description = "建立與取得訂單之API")
public class OrderController {
    @Autowired OrderService orderService;
    @Autowired ProductService productService;

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
    @PostMapping("/api/orders/add")
    public ResponseEntity<?> createOrder(@org.springframework.web.bind.annotation.RequestBody Order order) {
        try {
            if (order.getOrderType() == ProductTypes.AUCTION) {
                Cart.CartItem item = order.getCart().getItems().get(0);
                if (item == null) {
                    return ResponseEntity.badRequest().body("No Auction product in cart!");
                }
                Product auctionProduct = productService.getProductById(item.getProductId());

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
                        order.getBuyerID(),
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
                                                    "  \"orderID\": \"ORDER123\",\n" +
                                                    "  \"buyerID\": \"USER123\",\n" +
                                                    "  \"orderType\": \"DIRECT\"\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/api/orders/{orderID}")
    public ResponseEntity<?> getOrderById(
            @Parameter(description = "訂單ID", example = "69273ab8562988cda12ea950", required = true)
            @PathVariable String orderID

    ){
        try {
            return ResponseEntity.ok(orderService.getOrderById(orderID));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Order not found with ID: " + orderID);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}
