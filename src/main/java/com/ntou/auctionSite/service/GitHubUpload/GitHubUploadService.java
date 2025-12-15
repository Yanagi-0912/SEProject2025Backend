package com.ntou.auctionSite.service.GitHubUpload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class GitHubUploadService {

    @Value("${github.token}")
    private String token;

    @Value("${github.owner}")
    private String owner;

    @Value("${github.repo}")
    private String repo;

    @Value("${github.branch}")
    private String branch;

    private final RestTemplate restTemplate = new RestTemplate();

    public String upload(MultipartFile file) throws Exception {
        // Basic validations
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上傳檔案不可為空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException("僅允許上傳圖片檔案");
        }
        // Normalize filename to avoid path traversal
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        if (originalName.isEmpty()) {
            throw new IllegalArgumentException("檔名不可為空");
        }

        String fileName = UUID.randomUUID() + "-" + originalName;
        String path = "images/" + LocalDate.now().getYear() + "/" + fileName;
        String apiUrl = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                owner, repo, path
        );

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
            restTemplate.exchange(
                    apiUrl,
                    HttpMethod.PUT,
                    request,
                    String.class
            );
        } catch (HttpClientErrorException.Forbidden ex) {
            // 403: Forbidden
            String details = ex.getResponseBodyAsString();
            throw new IllegalStateException("GitHub API 403 Forbidden: " + details);
        } catch (HttpClientErrorException.UnprocessableEntity ex) {
            // 422: possibly file exists and requires sha
            // Try to GET the current file to retrieve sha then retry
            HttpEntity<Void> getReq = new HttpEntity<>(headers);
            org.springframework.http.ResponseEntity<String> getResp = restTemplate.exchange(
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
                throw new IllegalStateException("GitHub API 422: existing file sha not found. Body: " + bodyStr);
            }
            body = new java.util.HashMap<>(body);
            body.put("sha", sha);
            HttpEntity<Map<String, Object>> updateReq =
                    new HttpEntity<>(body, headers);
            restTemplate.exchange(
                    apiUrl,
                    HttpMethod.PUT,
                    updateReq,
                    String.class
            );
        } catch (HttpClientErrorException ex) {
            String details = ex.getResponseBodyAsString();
            throw new IllegalStateException("GitHub API error: " + ex.getStatusCode() + ": " + details);
        }

        return String.format(
                "https://cdn.jsdelivr.net/gh/%s/%s@%s/%s",
                owner, repo, branch, path
        );
    }
}
