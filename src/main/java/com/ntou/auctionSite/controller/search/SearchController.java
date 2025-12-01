package com.ntou.auctionSite.controller.search;

import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@Tag(name = "商品搜尋", description = "商品搜尋 API - 支援精確搜尋與模糊搜尋")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("api/search")
    @Operation(
            summary = "精確搜尋商品",
            description = "根據商品名稱進行精確搜尋，商品名稱需完全符合"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功找到符合的商品",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到符合的商品",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(mediaType = "text/plain")
            )
    })
    //@requestparam用來接住url候傳入的參數(?keyword=巧克力餅乾)
    public ResponseEntity<?> searchByKeyword(
            @Parameter(description = "搜尋關鍵字 (商品名稱)", required = true, example = "巧克力餅乾")
            @RequestParam String keyword){
        try{
            return ResponseEntity.ok(searchService.searchByKeyword(keyword.trim()));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("(Precise search)No result for keyword:" + keyword);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("api/blursearch")
    @Operation(
            summary = "模糊搜尋商品",
            description = "根據關鍵字進行模糊搜尋，可搜尋商品名稱、描述等相關內容"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功找到符合的商品",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "找不到符合的商品",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "伺服器錯誤",
                    content = @Content(mediaType = "text/plain")
            )
    })
    public ResponseEntity<?> blursearch(
            @Parameter(description = "搜尋關鍵字 (模糊比對)", required = true, example = "餅乾")
            @RequestParam String keyword){
        try{
            return ResponseEntity.ok(searchService.blurSearch(keyword.trim()));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("(Blursearch) No result for keyword:" + keyword);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}
