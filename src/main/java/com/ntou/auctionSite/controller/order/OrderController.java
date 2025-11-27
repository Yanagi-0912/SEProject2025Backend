package com.ntou.auctionSite.controller.order;

import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.order.Order;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import com.ntou.auctionSite.service.order.OrderService;
import com.ntou.auctionSite.service.product.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@Tag(name = "訂單管理", description = "建立與取得訂單之API")
public class OrderController {
    @Autowired OrderService orderService;
    @Autowired ProductService productService;

    @PostMapping("/api/orders/add")
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
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

    @GetMapping("/api/orders/{orderID}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderID){
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
