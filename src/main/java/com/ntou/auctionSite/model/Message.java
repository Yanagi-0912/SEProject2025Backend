package com.ntou.auctionSite.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "messages")
@Schema(description = "聊天訊息")
public class Message {
    @Id
    @Schema(description = "訊息唯一識別碼", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "聊天室識別碼", example = "1_2")
    private String chatId;

    @Schema(description = "發送者用戶 ID", example = "1")
    private String senderId;

    @Schema(description = "接收者用戶 ID", example = "2")
    private String recipientId;

    @Schema(description = "訊息內容", example = "你好，這個商品還有庫存嗎？")
    private String content;

    @Schema(description = "發送時間", example = "2025-12-16T10:30:00")
    private LocalDateTime timestamp;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getRecipientId() { return recipientId; } // 這就是報錯找不到的方法
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

}
