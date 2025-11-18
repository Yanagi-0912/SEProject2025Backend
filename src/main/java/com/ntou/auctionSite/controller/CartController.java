package com.ntou.auctionSite.controller;

import com.ntou.auctionSite.dto.AddToCartRequest;
import com.ntou.auctionSite.dto.UpdateCartQuantityRequest;
import com.ntou.auctionSite.model.Cart;
import com.ntou.auctionSite.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * 取得使用者購物車
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        String username = authentication.getName();
        Cart cart = cartService.getCart(username);
        return ResponseEntity.ok(cart);
    }

    /**
     * 加入商品到購物車
     * POST /api/cart/items
     */
    @PostMapping("/items")
    public ResponseEntity<Cart> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request
    ) {
        String username = authentication.getName();
        Cart cart = cartService.addToCart(username, request.productId(), request.quantity());
        return ResponseEntity.ok(cart);
    }

    /**
     * 更新購物車商品數量
     * PUT /api/cart/items/{productId}
     * 如果數量設為0，將移除該商品
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateQuantity(
            Authentication authentication,
            @PathVariable String productId,
            @Valid @RequestBody UpdateCartQuantityRequest request
    ) {
        String username = authentication.getName();
        Cart cart = cartService.updateQuantity(username, productId, request.quantity());
        return ResponseEntity.ok(cart);
    }

    /**
     * 從購物車移除商品
     * DELETE /api/cart/items/{productId}
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeFromCart(
            Authentication authentication,
            @PathVariable String productId
    ) {
        String username = authentication.getName();
        Cart cart = cartService.removeCart(username, productId);
        return ResponseEntity.ok(cart);
    }

    /**
     * 清空購物車
     * DELETE /api/cart
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String username = authentication.getName();
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
