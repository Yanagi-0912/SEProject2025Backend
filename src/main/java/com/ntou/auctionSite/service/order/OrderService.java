package com.ntou.auctionSite.service.order;

import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.coupon.Coupon;
import com.ntou.auctionSite.model.coupon.UserCoupon;
import com.ntou.auctionSite.model.order.Order;
import com.ntou.auctionSite.model.order.OrderItem;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import com.ntou.auctionSite.repository.OrderRepository;
import com.ntou.auctionSite.repository.ProductRepository;
import com.ntou.auctionSite.service.Coupon.CouponService;
import com.ntou.auctionSite.service.Coupon.UserCouponService;
import com.ntou.auctionSite.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.ntou.auctionSite.model.coupon.CouponType.*;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductService productService;
    @Autowired private CouponService couponService;
    @Autowired private UserCouponService userCouponService;
    //結帳功能:建立訂單、檢查與更新庫存
    public Order createOrder(Order order, String buyerID, ProductTypes types){
        Cart cart= order.getCart();
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice=0.0;
        double defaultShippingFee=100.0;
        for (Cart.CartItem item : cart.getItems()) {
            Product product = productService.getProductById(item.getProductId());
            if (product == null) {
                throw new NoSuchElementException("Product not found: " + item.getProductId());
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
                if(product.getProductStock()==0){//庫存數量變成0要設為INACTIVE
                    product.setProductStatus(Product.ProductStatuses.INACTIVE);
                }
                productRepository.save(product);

                orderItems.add(new OrderItem(
                        product.getProductID(),
                        item.getQuantity(),
                        product.getSellerID(),
                        product.getNowHighestBid()
                ));
            }
            else {//一般直購商品建立訂單

                if (product.getProductStock() < item.getQuantity()) {
                    throw new IllegalStateException("Out of stock! product: " + product.getProductName());
                }
                // 扣庫存
                product.setProductStock(product.getProductStock() - item.getQuantity());
                if(product.getProductStock()==0){
                    product.setProductStatus(Product.ProductStatuses.INACTIVE);
                }
                productRepository.save(product);

                orderItems.add(new OrderItem(
                        product.getProductID(),
                        item.getQuantity(),
                        product.getSellerID(),
                        product.getProductPrice()
                ));
            }
            totalPrice+=product.getProductPrice()*item.getQuantity();
        }
        //訂單ID設為隨機10碼
        order.setBuyerID(buyerID);
        order.setOrderID("ORD" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());//訂單用隨機id
        order.setOrderType(types);
        order.setOrderTime(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatuses.PENDING);
        order.setOrderItems(orderItems);
        order.setShippingFee(defaultShippingFee);//運費預設100元
        order.setTotalPrice(totalPrice+defaultShippingFee);
        return orderRepository.save(order);
    }

    //付款功能
    public Order payOrder(String orderID, String userCouponId){
        Order order = getOrderById(orderID);
        double totalPrice=order.getTotalPrice();
        // pending才能付款
        if(order.getOrderStatus() != Order.OrderStatuses.PENDING){
            throw new IllegalStateException("Order cannot be paid because it is not in PENDING status!");
        }

        // 套用優惠券 (如果有)，拍賣商品不適用優惠券
        if(userCouponId != null && !userCouponId.isEmpty() && order.getOrderType()!=ProductTypes.AUCTION){
            UserCoupon userCoupon = userCouponService.applyCoupon(order.getBuyerID(),userCouponId, orderID);

            // 從 userCoupon.getCouponID() 拿到 Coupon
            Coupon couponTemplate = couponService.getCouponById(userCoupon.getCouponID());

            double discountAmount = 0;
            switch (couponTemplate.getDiscountType()) {
                case PERCENT :
                    discountAmount = order.getTotalPrice() * (1 - couponTemplate.getDiscountValue());
                    break;
                case FIXED :
                    discountAmount = couponTemplate.getDiscountValue();
                    break;
                case FREESHIP:
                    discountAmount = order.getShippingFee(); // 假設有運費欄位
                    order.setShippingFee(0);
                    break;
                case BUY_ONE_GET_ONE:
                    applyBuyOneGetOneDiscount(order);
                    break;
            }
            if(totalPrice>couponTemplate.getMinPurchaseAmount()){
                order.setTotalPrice(order.getTotalPrice() - discountAmount);
            }
            else{
                throw new IllegalStateException("Order total does not meet the minimum purchase amount for this coupon!");
            }
        }
        else {
            order.setTotalPrice(order.getTotalPrice());
        }

        order.setOrderStatus(Order.OrderStatuses.COMPLETED);

        String buyerID = order.getBuyerID();
        userCouponService.issueCouponsAfterPay(buyerID); // 發送首購優惠券

        return orderRepository.save(order);
    }

    private void applyBuyOneGetOneDiscount(Order order) {
        //找出價格低於500的商品
        List<OrderItem> orderItems=order.getOrderItems();
        double maxPrice=500.0;//買一送一送的的商品價格最高500
        double currentMax=0.0;
        OrderItem sendProduct=null;
        for(OrderItem item:orderItems){
            if(item.getPrice()>currentMax && item.getPrice()<=maxPrice){
                currentMax=item.getPrice();
                sendProduct=item;
            }
        }
        if(sendProduct!=null){
            Product product=productService.getProductById(sendProduct.getProductID());
            if (product.getProductStock() < 1) {
                throw new IllegalStateException(
                        "Insufficient stock for BUY_ONE_GET_ONE product: " + product.getProductID()
                );
            }
            sendProduct.setQuantity(sendProduct.getQuantity() + 1);
            product.setProductStock(product.getProductStock() - 1);//更新庫存
            if (product.getProductStock() == 0) {
                product.setProductStatus(Product.ProductStatuses.INACTIVE);
            }
            productRepository.save(product);

        }
        else{
            throw new IllegalStateException("No suitable product for buy one get one");
        }
    }

    public Order getOrderById(String orderID){
            return orderRepository.findById(orderID)
                    .orElseThrow(() -> new NoSuchElementException("Order not found with orderID: " + orderID));
    }
}

