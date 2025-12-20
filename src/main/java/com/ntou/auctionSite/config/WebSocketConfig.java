package com.ntou.auctionSite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override // 設定訊息代理
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");// 用於伺服器發送訊息的前綴
        config.setApplicationDestinationPrefixes("/app");             // 用於客戶端發送訊息的前綴
    }

    @Override // 註冊 STOMP 端點
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")     // WebSocket 端點
                .setAllowedOriginPatterns("*") // 允許所有來源
                .withSockJS();                 // 啟用 SockJS 支援
    }
}
