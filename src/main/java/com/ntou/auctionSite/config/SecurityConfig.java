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
            .csrf(csrf -> csrf.disable())                          // 禁用 CSRF 保護
            .authorizeHttpRequests( auth -> { auth
                    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()  // 允許所有人訪問商品列表和詳情
                    .requestMatchers(HttpMethod.GET, "/api/user/*").permitAll()       // 允許訪問使用者公開資訊（含聊天功能）
                    .requestMatchers("/api/products/**").authenticated()              // 其他商品相關請求需要認證
                    .requestMatchers("/api/auth/**").permitAll()                      // 允許所有人訪問認證相關的端點
                    .requestMatchers("/api/search", "/api/blursearch").permitAll()    // 允許所有人訪問搜尋端點
                    .requestMatchers(HttpMethod.GET, "/api/history/**").permitAll()   // 允許所有人查詢歷史記錄
                    .requestMatchers(HttpMethod.POST, "/api/history/**").authenticated() // POST 歷史記錄需要認證
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // 允許訪問 Swagger UI
                    .requestMatchers("/ws/**").permitAll() // 允許 WebSocket 連線
                    .anyRequest().authenticated();                                             // 其他請求需要認證
                    }
            )
            .sessionManagement(session -> session. // 無狀態 session
                    sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
