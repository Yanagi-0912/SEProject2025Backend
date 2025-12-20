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
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "圖片上傳", description = "圖片上傳至 GitHub 並回傳 CDN URL")
public class ImageUploadController {

    private static final Logger logger = Logger.getLogger(ImageUploadController.class.getName());

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
                schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "500", description = "伺服器內部錯誤（GitHub API 失敗）",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = java.util.Map.class)))
    })
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "要上傳的圖片檔（二進位）",
                schema = @Schema(type = "string", format = "binary"))
            @RequestPart("file") MultipartFile file
    ) {
        try {
            logger.info("開始處理圖片上傳請求，檔案: " + (file != null ? file.getOriginalFilename() : "null"));

            if (file == null) {
                logger.warning("接收到 null 的檔案");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "缺少 file 欄位"));
            }

            if (file.isEmpty()) {
                logger.warning("上傳的檔案為空");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "上傳檔案不可為空"));
            }

            logger.info("檔案大小: " + file.getSize() + " bytes");
            logger.info("檔案類型: " + file.getContentType());

            String imageUrl = gitHubUploadService.upload(file);
            logger.info("圖片上傳成功: " + imageUrl);

            return ResponseEntity.ok(Map.of(
                    "url", imageUrl,
                    "filename", file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown",
                    "size", file.getSize()
            ));
        } catch (IllegalArgumentException ex) {
            logger.warning("驗證錯誤: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", ex.getMessage(),
                            "type", "VALIDATION_ERROR"
                    ));
        } catch (IllegalStateException ex) {
            logger.severe("GitHub API 錯誤: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", ex.getMessage(),
                            "type", "GITHUB_API_ERROR"
                    ));
        } catch (Exception ex) {
            logger.severe("未預期的錯誤: " + ex.getMessage());
            logger.severe("錯誤堆棧: " + ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "上傳失敗: " + ex.getMessage(),
                            "type", "UNEXPECTED_ERROR"
                    ));
        }
    }
}
