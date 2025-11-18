package com.ntou.auctionSite.controller;

import com.ntou.auctionSite.model.Cart;
import com.ntou.auctionSite.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication){
        String username = authentication.getName();
        Cart cart = cartService.getCart(username);
        return ResponseEntity.ok(cart);

    }
}
