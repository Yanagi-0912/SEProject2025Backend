package com.ntou.auctionSite.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chat_rooms")
@Schema(description = "聊天室實體（用於管理用戶間的對話關係）")
public class ChatRoom {
    @Id
    @Schema(description = "聊天室唯一識別碼（MongoDB 自動生成）", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "聊天室代號（格式：senderId_recipientId）", example = "1_2")
    private String chatId;

    @Schema(description = "發送者用戶 ID", example = "1")
    private String senderId;

    @Schema(description = "接收者用戶 ID", example = "2")
    private String recipientId;
}

