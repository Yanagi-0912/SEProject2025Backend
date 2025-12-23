package com.ntou.auctionSite.service.cart;

import com.ntou.auctionSite.dto.cart.CartItemDTO;
import com.ntou.auctionSite.dto.cart.CartResponseDTO;
import com.ntou.auctionSite.model.cart.Cart;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.CartRepository;
import com.ntou.auctionSite.repository.ProductRepository;
import com.ntou.auctionSite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 取得使用者購物車（完整資訊）
     */
    public CartResponseDTO getUserCart(String userId) {
        // 1. 從資料庫查詢購物車
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(userId, userId, new ArrayList<>()));

        // 2. 組合每個項目的完整資訊（商品資訊 + 賣家資訊）
        // 過濾掉已刪除的商品，避免拋出異常
        List<CartItemDTO> items = cart.getItems().stream()
                .map(item -> {
                    // 檢查商品是否存在
                    Product product = productRepository.findById(item.getProductId())
                            .orElse(null);
                    
                    if (product == null) {
                        // 商品不存在，返回 null，稍後過濾掉
                        return null;
                    }

                    // 檢查賣家是否存在
                    User seller = userRepository.findById(product.getSellerID())
                            .orElse(null);
                    
                    if (seller == null) {
                        // 賣家不存在，返回 null，稍後過濾掉
                        return null;
                    }

                    return new CartItemDTO(
                            item.getItemId(),              // 項目 ID
                            product.getProductID(),        // 商品 ID
                            product.getProductName(),      // 商品名稱
                            product.getProductPrice(),     // 商品價格
                            product.getProductImage(),     // 商品圖片
                            seller.getId(),                // 賣家 ID
                            seller.getUsername(),          // 賣家名稱
                            item.getQuantity(),            // 數量
                            product.getProductPrice() * item.getQuantity()  // 小計
                    );
                })
                .filter(item -> item != null)  // 過濾掉 null（已刪除的商品）
                .collect(Collectors.toList());

        // 3. 如果購物車中有已刪除的商品，更新購物車（移除已刪除的商品）
        if (items.size() < cart.getItems().size()) {
            // 重新建立購物車，只保留有效的商品
            List<Cart.CartItem> validItems = cart.getItems().stream()
                    .filter(item -> {
                        Product product = productRepository.findById(item.getProductId()).orElse(null);
                        if (product == null) return false;
                        User seller = userRepository.findById(product.getSellerID()).orElse(null);
                        return seller != null;
                    })
                    .collect(Collectors.toList());
            
            cart.setItems(validItems);
            cartRepository.save(cart);
        }

        // 4. 計算總金額
        int totalAmount = items.stream()
                .mapToInt(CartItemDTO::getSubtotal)
                .sum();

        return new CartResponseDTO(userId, items, totalAmount, items.size());
    }

    /**
     * 將商品加入購物車
     */
    public Cart addToCart(String userId, String productId, int quantity) {
        // 驗證商品是否存在
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));

        // 查詢或建立購物車
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(userId, userId, new ArrayList<>()));

        // 檢查商品是否已存在於購物車
        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // 已存在則累加數量
            Cart.CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // 不存在則新增項目（會自動生成 itemId）
            cart.getItems().add(new Cart.CartItem(productId, quantity));
        }

        // 儲存到資料庫
        return cartRepository.save(cart);
    }

    /**
     * 更新商品數量（透過 itemId）
     */
    public Cart updateQuantity(String userId, String itemId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("購物車不存在"));

        Cart.CartItem item = cart.getItems().stream()
                .filter(i -> i.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("購物車找不到該商品項目"));

        if (quantity <= 0) {
            // 數量 <= 0 則移除項目
            cart.getItems().remove(item);
        } else {
            // 更新數量
            item.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

    /**
     * 從購物車移除商品（透過 itemId）
     */
    public Cart removeItem(String userId, String itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("購物車不存在"));

        cart.getItems().removeIf(item -> item.getItemId().equals(itemId));

        return cartRepository.save(cart);
    }

    /**
     * 清空購物車
     */
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("購物車不存在"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
