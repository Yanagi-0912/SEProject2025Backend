package com.ntou.auctionSite.service.order;

import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.order.Order;
import com.ntou.auctionSite.model.order.OrderItem;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import com.ntou.auctionSite.repository.OrderRepository;
import com.ntou.auctionSite.repository.ProductRepository;
import com.ntou.auctionSite.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductService productService;
    //結帳功能:建立訂單、檢查與更新庫存
    public Order createOrder(Order order, String buyerID, ProductTypes types){
        Cart cart= order.getCart();
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart.CartItem item : cart.getItems()) {
            Product product = productService.getProductById(item.getProductID());
            if (product == null) {
                throw new NoSuchElementException("Product not found: " + item.getProductID());
            }

            if (types == ProductTypes.AUCTION) {//拍賣商品建立訂單
                if (product.getProductStock() < item.getQuantity()) {
                    throw new IllegalStateException("Out of stock! product: " + product.getProductName());
                }
                // 先前競拍成功時，會將該商品設為SOLD
                if (product.getProductStatus() != Product.ProductStatuses.SOLD
                        && product.getHighestBidderID() == null) {
                    throw new IllegalStateException(
                            "The auction has not yet completed. Product ID: " + product.getProductID());
                }
                // 扣庫存
                product.setProductStock(product.getProductStock() - item.getQuantity());
                productRepository.save(product);

                orderItems.add(new OrderItem(
                        product.getProductID(),
                        item.getQuantity(),
                        product.getSellerID(),
                        product.getNowHighestBid()
                ));
            } else {//一般直購商品建立訂單

                if (product.getProductStock() < item.getQuantity()) {
                    throw new IllegalStateException("Out of stock! product: " + product.getProductName());
                }
                // 扣庫存
                product.setProductStock(product.getProductStock() - item.getQuantity());
                productRepository.save(product);

                orderItems.add(new OrderItem(
                        product.getProductID(),
                        item.getQuantity(),
                        product.getSellerID(),
                        product.getProductPrice()
                ));
            }
        }
        //訂單ID設為隨機10碼
        order.setOrderID(UUID.randomUUID().toString().substring(0, 10).toUpperCase());//訂單用隨機id
        order.setOrderType(types);
        order.setOrderTime(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatuses.PENDING);
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

    public Order getOrderById(String orderID){
            return orderRepository.findByOrderID(orderID)
                    .orElseThrow(() -> new NoSuchElementException("Order not found with orderID: " + orderID));
    }


}

