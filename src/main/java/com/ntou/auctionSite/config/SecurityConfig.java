//
package com.ntou.auctionSite.config;

import com.ntou.auctionSite.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 啟用 CORS
            .csrf(csrf -> csrf.disable())                                       // 禁用 CSRF 保護
            .authorizeHttpRequests(auth -> auth
                    // ========== 公開端點 (不需要認證) ==========
                    // 認證相關
                    .requestMatchers("/api/auth/**").permitAll()

                    // 文件與 API 文件
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                    // WebSocket 連線
                    .requestMatchers("/ws/**").permitAll()

                    // 商品相關 (僅查詢)
                    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                    // 用戶相關 (僅查詢)
                    .requestMatchers(HttpMethod.GET, "/api/user/*").permitAll()

                    // 訂單相關 (僅查詢)
                    .requestMatchers(HttpMethod.GET, "/api/orders/**").permitAll()

                    // 歷史記錄 (僅查詢)
                    .requestMatchers(HttpMethod.GET, "/api/history/**").permitAll()

                    // 搜尋功能
                    .requestMatchers("/api/search", "/api/blursearch").permitAll()

                    // 檔案上傳
                    .requestMatchers(HttpMethod.POST, "/api/upload/**").permitAll()

                    // ========== 需要認證的端點 ==========
                    // 當前用戶資訊
                    .requestMatchers(HttpMethod.GET, "/api/user/me").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/user/me").authenticated()

                    // 商品相關 (新增、修改、刪除)
                    .requestMatchers("/api/products/**").authenticated()

                    // 歷史記錄 (新增)
                    .requestMatchers(HttpMethod.POST, "/api/history/**").authenticated()

                    // 其他所有請求
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 無狀態 session
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 加入 JWT Filter
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允許所有來源 (開發測試用，生產環境請限制)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // 允許的 HTTP 方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 允許所有標頭
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 允許傳送憑證 (JWT Token)
        configuration.setAllowCredentials(true);

        // 預檢請求的有效期 (1小時)
        configuration.setMaxAge(3600L);

        // 暴露的響應標頭
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // 認證管理器
    }
}
