package com.ntou.auctionSite.service.GitHubUpload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class GitHubUploadService {

    private static final Logger logger = Logger.getLogger(GitHubUploadService.class.getName());

    @Value("${github.token}")
    private String token;

    @Value("${github.owner}")
    private String owner;

    @Value("${github.repo}")
    private String repo;

    @Value("${github.branch}")
    private String branch;

    @Autowired
    private RestTemplate restTemplate;

    public String upload(MultipartFile file) throws Exception {
        logger.info("開始上傳圖片，檔案名稱: " + file.getOriginalFilename());

        // Basic validations
        if (file == null || file.isEmpty()) {
            logger.warning("上傳檔案為空");
            throw new IllegalArgumentException("上傳檔案不可為空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            logger.warning("檔案類型不是圖片: " + contentType);
            throw new IllegalArgumentException("僅允許上傳圖片檔案，當前類型: " + contentType);
        }

        // Normalize filename to avoid path traversal
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        if (originalName.isEmpty()) {
            logger.warning("檔名為空");
            throw new IllegalArgumentException("檔名不可為空");
        }

        String fileName = UUID.randomUUID() + "-" + originalName;
        String path = "images/" + LocalDate.now().getYear() + "/" + fileName;
        String apiUrl = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                owner, repo, path
        );

        logger.info("GitHub API URL: " + apiUrl);

        String contentBase64 = Base64.getEncoder()
                .encodeToString(file.getBytes());

        Map<String, Object> body = Map.of(
                "message", "upload image " + fileName,
                "content", contentBase64,
                "branch", branch
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("User-Agent", "SEProject2025Backend");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.PUT,
                    request,
                    String.class
            );
            logger.info("GitHub API 上傳成功，狀態碼: " + response.getStatusCode());
        } catch (HttpClientErrorException.Forbidden ex) {
            // 403: Forbidden
            String details = ex.getResponseBodyAsString();
            logger.severe("GitHub API 403 Forbidden: " + details);
            throw new IllegalStateException("GitHub API 403 Forbidden: " + details);
        } catch (HttpClientErrorException.UnprocessableEntity ex) {
            // 422: possibly file exists and requires sha
            logger.info("GitHub API 422 檔案已存在，嘗試更新...");

            // Try to GET the current file to retrieve sha then retry
            HttpEntity<Void> getReq = new HttpEntity<>(headers);
            try {
                ResponseEntity<String> getResp = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.GET,
                        getReq,
                        String.class
                );
                // naive JSON parse to extract sha
                String bodyStr = getResp.getBody();
                String sha = null;
                if (bodyStr != null) {
                    int idx = bodyStr.indexOf("\"sha\":\"");
                    if (idx >= 0) {
                        int start = idx + 7;
                        int end = bodyStr.indexOf('"', start);
                        if (end > start) sha = bodyStr.substring(start, end);
                    }
                }
                if (sha == null) {
                    logger.severe("無法從 GitHub API 響應中提取 sha");
                    throw new IllegalStateException("GitHub API 422: existing file sha not found. Body: " + bodyStr);
                }

                Map<String, Object> updateBody = new java.util.HashMap<>(body);
                updateBody.put("sha", sha);
                HttpEntity<Map<String, Object>> updateReq =
                        new HttpEntity<>(updateBody, headers);

                ResponseEntity<String> updateResp = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.PUT,
                        updateReq,
                        String.class
                );
                logger.info("GitHub API 更新成功，狀態碼: " + updateResp.getStatusCode());
            } catch (Exception updateEx) {
                logger.severe("GitHub API 更新失敗: " + updateEx.getMessage());
                throw updateEx;
            }
        } catch (HttpClientErrorException ex) {
            String details = ex.getResponseBodyAsString();
            logger.severe("GitHub API 錯誤: " + ex.getStatusCode() + ": " + details);
            throw new IllegalStateException("GitHub API error: " + ex.getStatusCode() + ": " + details);
        }

        String cdnUrl = String.format(
                "https://cdn.jsdelivr.net/gh/%s/%s@%s/%s",
                owner, repo, branch, path
        );
        logger.info("圖片 CDN URL: " + cdnUrl);

        return cdnUrl;
    }
}
