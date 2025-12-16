package com.ntou.auctionSite.controller;

import com.ntou.auctionSite.model.Message;
import com.ntou.auctionSite.model.ChatNotification;
import com.ntou.auctionSite.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "聊天功能", description = "聊天室相關 API，支援 WebSocket 即時通訊和歷史訊息查詢")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    @Operation(
            summary = "發送即時訊息（WebSocket）",
            description = "透過 WebSocket 發送即時訊息給指定用戶。前端需連線到 /ws 並發送到 /app/chat"
    )
    public void processMessage(@Payload Message chatMessage) {
        Message savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getRecipientId()), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    @Operation(
            summary = "查詢聊天歷史",
            description = "查詢兩個用戶之間的所有聊天訊息"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功取得聊天記錄"),
            @ApiResponse(responseCode = "404", description = "找不到聊天記錄")
    })
    public ResponseEntity<List<Message>> findChatMessages(
            @Parameter(description = "發送者用戶 ID", required = true, example = "1")
            @PathVariable Long senderId,
            @Parameter(description = "接收者用戶 ID", required = true, example = "2")
            @PathVariable Long recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
}

