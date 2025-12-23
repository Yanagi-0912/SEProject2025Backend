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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.ntou.auctionSite.model.coupon.CouponType.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;
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
        double allProductTotal = order.getTotalPrice() - order.getShippingFee();
        double shipFee = order.getShippingFee();
        double discountAmount = 0;

        if(order.getOrderStatus() != Order.OrderStatuses.PENDING){
            throw new IllegalStateException("Order cannot be paid because it is not in PENDING status!");
        }

        if(userCouponId != null && !userCouponId.isEmpty() && order.getOrderType() != ProductTypes.AUCTION){
            UserCoupon userCoupon = userCouponService.applyCoupon(order.getBuyerID(), userCouponId, orderID);
            Coupon couponTemplate = couponService.getCouponById(userCoupon.getCouponID());

            switch(couponTemplate.getDiscountType()){
                case PERCENT:
                    discountAmount = allProductTotal * ( couponTemplate.getDiscountValue());
                    break;
                case FIXED:
                    discountAmount = Math.min(couponTemplate.getDiscountValue(), allProductTotal);
                    break;
                case FREESHIP:
                    discountAmount = shipFee;
                    shipFee = 0;
                    order.setShippingFee(0);
                    break;
                case BUY_ONE_GET_ONE:
                    discountAmount = applyBuyOneGetOneDiscount(order);
                    break;
            }

            if(allProductTotal >= couponTemplate.getMinPurchaseAmount()){
                if(couponTemplate.getDiscountType() == FREESHIP){
                    order.setTotalPrice(allProductTotal + shipFee);
                }
                else {
                    order.setTotalPrice(allProductTotal - discountAmount + shipFee);
                }
            }
            else {
                throw new IllegalStateException("Order total does not meet the minimum purchase amount for this coupon!");
            }
        }

        order.setOrderStatus(Order.OrderStatuses.COMPLETED);
        userCouponService.issueCouponsAfterPay(order.getBuyerID());

        return orderRepository.save(order);
    }


    private double applyBuyOneGetOneDiscount(Order order) {
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
            // createOrder 已經扣了買的那 1 件，這裡只需要扣送的那 1 件
            if (product.getProductStock() < 1) {
                throw new IllegalStateException(
                        "Insufficient stock for BUY_ONE_GET_ONE product: " + product.getProductID()
                );
            }
            sendProduct.setQuantity(sendProduct.getQuantity() + 1);
            product.setProductStock(product.getProductStock() - 1);//更新庫存：只扣送的 1 件（買的已在 createOrder 扣了）
            if (product.getProductStock() == 0) {
                product.setProductStatus(Product.ProductStatuses.INACTIVE);
            }
            productRepository.save(product);
            
            // 返回送的商品價格作為折扣金額
            return sendProduct.getPrice();
        }
        else{
            throw new IllegalStateException("No suitable product for buy one get one");
        }
    }

    public Order getOrderById(String orderID){
            // 使用 findByOrderID 明確查詢 orderID 欄位，而不是依賴 findById（可能查詢 _id）
            return orderRepository.findByOrderID(orderID.trim())
                    .orElseThrow(() -> new NoSuchElementException("Order not found with orderID: " + orderID));
    }

    public List<Order> getOrderByBuyerId(String buyerID){//根據傳入者的ID回傳訂單
        try{
            List<Order> orderList=orderRepository.findByBuyerID(buyerID);
            if(orderList.isEmpty()){
                throw new NoSuchElementException("No order found! buyerID: "+buyerID);
            }
            return orderList;
        }
        catch(Exception e){
            System.err.println("Error fetching orders: " + e.getMessage());
            return Collections.emptyList();//回傳一個不可更改的空list
        }
    }
}

