package com.ntou.auctionSite.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "聊天即時通知（透過 WebSocket 推送）")
public class ChatNotification {
    @Schema(description = "訊息 ID", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "發送者用戶 ID", example = "1")
    private Long senderId;

    @Schema(description = "接收者用戶 ID", example = "2")
    private Long recipientId;

    @Schema(description = "訊息內容", example = "你好，這個商品還有庫存嗎？")
    private String content;
}

