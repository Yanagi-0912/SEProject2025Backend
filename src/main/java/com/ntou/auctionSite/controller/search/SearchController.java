package com.ntou.auctionSite.controller.search;

import com.ntou.auctionSite.service.search.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@Tag(name = "關鍵字搜尋", description = "依據關鍵字搜尋商品")
public class SearchController {
    @Autowired
    private SearchService searchService;
    //@requestparam用來接住url候傳入的參數(?keyword=巧克力餅乾)
    @GetMapping("api/search")
    public ResponseEntity<?> searchByKeyword(@RequestParam String keyword){
        try{
            return ResponseEntity.ok(searchService.searchByKeyword(keyword));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("(Precise search)No result for keyword:" + keyword);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("api/blursearch")
    public ResponseEntity<?> blursearch(@RequestParam String keyword){
        try{
            return ResponseEntity.ok(searchService.blurSearch(keyword));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("(Blursearch) No result for keyword:" + keyword);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}
