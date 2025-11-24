package com.ntou.auctionSite.service.cart;

import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class CartService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 取得使用者購物車
     */
    public Cart getCart(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("找不到用戶，user id: " + username));
        return user.getCart();
    }

    /**
     * 將商品加入使用者購物車
     */
    public Cart addToCart(String username, String productId, int quantity) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("找不到用戶，username: " + username));
        Cart cart = user.getCart();

        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(cartItem ->  cartItem.getProductID().equals(productId))
                .findFirst();
        if (existingItem.isPresent()){
            Cart.CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        }else{
            cart.getItems().add(new Cart.CartItem(productId, quantity));
        }

        userRepository.save(user);
        return cart;
    }

    /**
     * 更新商品數量
     */
    public Cart updateQuantity(String username, String productId, int quantity){
        User user = userRepository.findByUserName(username)
                .orElseThrow(()->new RuntimeException("找不到用戶，username: " + username));
        Cart cart = user.getCart();

        Cart.CartItem item = cart.getItems().stream()
                .filter((i -> i.getProductID().equals(productId)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("購物車找不到該商品"));

        if (quantity <= 0){
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        userRepository.save(user);
        return cart;
    }

    /**
     * 從使用者購物車移除商品
     */
    public Cart removeCart(String username, String productId){
        User user = userRepository.findByUserName(username)
                .orElseThrow(()->new RuntimeException("找不到用戶，username: " + username));
        Cart cart = user.getCart();

        cart.getItems().removeIf(item -> item.getProductID().equals(productId));

        userRepository.save(user);
        return cart;
    }

    public void clearCart(String username){
        User user = userRepository.findByUserName(username)
                .orElseThrow(()->new RuntimeException("找不到用戶，username: " + username));

        user.getCart().getItems().clear();
        userRepository.save(user);
    }


    private User getUserByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("找不到用戶，username: " + username));
    }

}

