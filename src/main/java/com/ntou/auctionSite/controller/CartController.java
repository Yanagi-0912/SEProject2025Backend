package com.ntou.auctionSite.controller;

import com.ntou.auctionSite.dto.AddToCartRequest;
import com.ntou.auctionSite.dto.UpdateCartQuantityRequest;
import com.ntou.auctionSite.model.Cart;
import com.ntou.auctionSite.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "購物車管理", description = "購物車相關 API - 加入商品、更新數量、移除商品等功能")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {
    private final CartService cartService;

    /**
     * 取得使用者購物車
     * GET /api/cart
     */
    @GetMapping
    @Operation(
            summary = "取得購物車",
            description = "取得當前登入使用者的購物車內容，包含所有已加入的商品及數量"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得購物車",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cart.class),
                            examples = @ExampleObject(
                                    value = "{\"items\":[{\"productID\":\"product123\",\"quantity\":2},{\"productID\":\"product456\",\"quantity\":1}]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - 需要登入",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到使用者",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "找不到用戶，user id: xxx")
                    )
            )
    })
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
    @Operation(
            summary = "加入商品到購物車",
            description = "將指定商品加入購物車。如果商品已存在，則會增加數量；如果不存在，則會新增該商品"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功加入商品到購物車",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cart.class),
                            examples = @ExampleObject(
                                    value = "{\"items\":[{\"productID\":\"product123\",\"quantity\":3}]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "請求資料格式錯誤",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"productId\":\"商品ID不可為空\",\"quantity\":\"數量必須大於0\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - 需要登入",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到使用者",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "找不到用戶，username: xxx")
                    )
            )
    })
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
    @Operation(
            summary = "更新購物車商品數量",
            description = "更新購物車中指定商品的數量。如果數量設為 0 或負數，該商品會自動從購物車中移除"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功更新商品數量",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cart.class),
                            examples = @ExampleObject(
                                    value = "{\"items\":[{\"productID\":\"product123\",\"quantity\":5}]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "請求資料格式錯誤",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"quantity\":\"數量不可為負數\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - 需要登入",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到使用者或商品不在購物車中",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "購物車找不到該商品")
                    )
            )
    })
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
    @Operation(
            summary = "從購物車移除商品",
            description = "從購物車中移除指定的商品"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功移除商品",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cart.class),
                            examples = @ExampleObject(
                                    value = "{\"items\":[{\"productID\":\"product456\",\"quantity\":1}]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - 需要登入",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到使用者",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "找不到用戶，username: xxx")
                    )
            )
    })
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
    @Operation(
            summary = "清空購物車",
            description = "清空購物車中的所有商品"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "成功清空購物車",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授權 - 需要登入",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到使用者",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "找不到用戶，username: xxx")
                    )
            )
    })
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String username = authentication.getName();
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
