package com.ntou.auctionSite.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 配置
 * 訪問路徑: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 定義 JWT Security Scheme
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                // API 基本資訊
                .info(new Info()
                        .title("NTOU 拍賣系統 API")
                        .description("海大拍賣系統後端 API 文檔 - 提供註冊、登入、商品管理等功能")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("NTOU SE Team")
                                .email("support@ntou-auction.com")))

                // 伺服器資訊
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("本地開發伺服器"),
                        new Server()
                                .url("https://your-production-url.onrender.com")
                                .description("生產環境伺服器（Render）")
                ))

                // JWT 認證配置
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("請在下方輸入從登入 API 獲得的 JWT Token（不需要加 'Bearer ' 前綴）")));
    }
}

