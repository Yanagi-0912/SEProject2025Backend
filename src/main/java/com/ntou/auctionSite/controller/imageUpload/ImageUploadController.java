package com.ntou.auctionSite.controller.imageUpload;

import com.ntou.auctionSite.service.GitHubUpload.GitHubUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "圖片上傳", description = "圖片上傳至 GitHub 並回傳 CDN URL")
public class ImageUploadController {

    private final GitHubUploadService gitHubUploadService;

    public ImageUploadController(GitHubUploadService service) {
        this.gitHubUploadService = service;
    }

    @PostMapping(path = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "上傳圖片",
        description = "透過 multipart/form-data 上傳圖片檔，檔案會儲存到 GitHub 並回傳 CDN URL"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上傳成功，回傳圖片 CDN URL",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "400", description = "請求錯誤（非圖片或檔案為空或缺少 file 欄位）",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = java.util.Map.class)))
    })
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "要上傳的圖片檔（二進位）",
                schema = @Schema(type = "string", format = "binary"))
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        try {
            String imageUrl = gitHubUploadService.upload(file);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
