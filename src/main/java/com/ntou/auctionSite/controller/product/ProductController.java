package com.ntou.auctionSite.controller.product;

import java.util.*;

import com.ntou.auctionSite.dto.product.EditProductRequest;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.repository.UserRepository;
import com.ntou.auctionSite.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ntou.auctionSite.service.product.ProductService;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/api/products")
@Tag(name = "商品管理", description = "商品相關 API - 新增、查詢、修改、上下架、刪除商品等功能")
public class ProductController { // 負責處理商品新增、上下架、查看、修改的class
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    //<?>表示可以是任何型態,前端可以提供第幾頁、每頁大小
    @GetMapping
    @Operation(
            summary = "取得商品列表（分頁）",
            description = "分頁查詢所有商品，支援自訂每頁商品數量"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得商品列表",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    value = "[{\"productID\":\"P001\",\"productName\":\"餅乾\",\"productPrice\":100,\"productType\":\"DIRECT\",\"productStatus\":\"AVAILABLE\"}]"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Error fetching products: xxx")
                    )
            )
    })
    public ResponseEntity<?> getAllProduct(
            @Parameter(description = "頁碼（從1開始）", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每頁商品數量", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Product> products = productService.getProductsByPage(page, pageSize);
            return ResponseEntity.ok(products);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching products: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "取得單一商品資訊",
            description = "根據商品 ID 查詢商品詳細資訊"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功取得商品資訊",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    value = "{\"productID\":\"P001\",\"sellerID\":\"S001\",\"productName\":\"餅乾\",\"productPrice\":100,\"productType\":\"DIRECT\",\"productStatus\":\"AVAILABLE\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product not found with ID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> getProductById(
            @Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable String id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/add") // 新增商品
    @Operation(
            summary = "新增商品",
            description = "建立新商品，商品預設狀態為 PENDING（待上架）"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "商品建立成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product created successfully! ProductID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "請求資料格式錯誤或商品資料不符合規定",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Error creating product: 商品名稱不可為空")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "商品資料",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    value = "{\"productName\":\"餅乾\",\"productPrice\":100,\"productType\":\"DIRECT\"}"
                            )
                    )
            )
            @RequestBody Product product,
            Authentication authentication
    ) {

        try {
            // 從 Authentication 拿到目前使用者的 username 或 userId
            String username= authentication.getName(); // 或用 userService 查出完整 User
            String currentUserId = userService.getUserInfo(username).id();
            System.out.println(currentUserId);
            Product saved = productService.createProduct(product, currentUserId);
            return ResponseEntity.status(201)
                    .body("Product created successfully! ProductID: " + saved.getProductID());
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("Error creating product: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }


    @PutMapping("/edit/{productID}") // 修改商品
    @Operation(
            summary = "修改商品資訊",
            description = "更新商品的基本資訊（名稱、價格、描述等）"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "商品更新成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product updated successfully! ProductID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product not found with ID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "沒有權限修改商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "You are not authorized to edit this product")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> editProduct(
            @PathVariable String productID,
            @RequestBody EditProductRequest request,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            String currentUserId = userService.getUserInfo(username).id();
            Product updated = productService.editProduct(productID, request, currentUserId);
            return ResponseEntity.ok("Product updated successfully! ProductID: " + updated.getProductID());

        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productID);
        }
        catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }


    @PutMapping("/upload/{productID}") // 上架商品
    @Operation(
            summary = "上架商品",
            description = "將商品狀態改為 AVAILABLE（已上架），使商品可供購買"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "商品上架成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product published successfully! ProductID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product not found with ID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> publishProduct(
            @Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable String productID,Authentication authentication) {
        try {
            String username = authentication.getName();
            String currentUserId = userService.getUserInfo(username).id();
            Product published = productService.publishProduct(productID,currentUserId);
            System.out.println(username);
            System.out.println(currentUserId);
            return ResponseEntity.ok("Product published successfully! ProductID: " + published.getProductID());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productID);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/withdraw/{productID}") // 下架商品
    @Operation(
            summary = "下架商品",
            description = "將商品狀態改為 UNAVAILABLE（已下架），商品將不可購買"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "商品下架成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product withdrawn successfully! ProductID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product not found with ID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> withdrawProduct(
            //@Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable String productID,Authentication authentication) {
        try {
            String username = authentication.getName();
            String currentUserId = userService.getUserInfo(username).id();
            Product withdrawn = productService.withdrawProduct(productID,currentUserId);
            return ResponseEntity.ok("Product withdrawn successfully! ProductID: " + withdrawn.getProductID());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productID);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete/{productID}")//刪除產品
    @Operation(
            summary = "刪除商品",
            description = "永久刪除商品資料"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "商品刪除成功",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product deleted successfully! ProductID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到商品",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Product not found with ID: P001")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Server error: xxx")
                    )
            )
    })
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "商品ID", example = "P001", required = true)
            @PathVariable String productID,Authentication authentication) {
        try {
            String username=authentication.getName();
            String currentUserId=userService.getUserInfo(username).id();
            productService.deleteProduct(productID,currentUserId);
            return ResponseEntity.ok("Product deleted successfully! ProductID: " + productID);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productID);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}
