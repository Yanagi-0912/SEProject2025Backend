/*
* 登入成功 → generateToken() → 回傳 JWT 給前端
*                                    ↓
/ 前端儲存 JWT → 每次請求帶上 JWT → extractUsername() → 驗證身份
*                                    ↓
*                              isTokenValid() → 允許/拒絕請求
*/

package com.ntou.auctionSite.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 從 Token 中提取使用者名稱
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 從 Token 中提取特定資訊
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 產生 Token
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // 產生 Token(帶額外資訊)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    // 建立 Token (包含 Claims、主體、簽發時間、過期時間、簽名)
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    // 驗證 Token 是否有效
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // 檢查 Token 是否過期
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 提取過期時間
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 提取所有 Claims
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()                               // 建立解析器
                .verifyWith((SecretKey) getSignInKey()) // 設定簽名金鑰
                .build()                                // 建立解析器實例
                .parseSignedClaims(token)               // 解析 Token
                .getPayload();                          // 獲取 Claims
    }
    // 取得簽名金鑰
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // 解碼 Base64 編碼的密鑰
        return Keys.hmacShaKeyFor(keyBytes);                 // 建立 HMAC SHA 金鑰 (HMAC SHA : 一種對稱加密演算法)
    }
}
