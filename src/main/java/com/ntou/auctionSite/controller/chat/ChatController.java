package com.ntou.auctionSite.controller.chat;

import com.ntou.auctionSite.model.ChatNotification;
import com.ntou.auctionSite.model.ChatRoom; // 記得 import 這個
import com.ntou.auctionSite.model.Message;
import com.ntou.auctionSite.service.ChatMessageService;
import com.ntou.auctionSite.service.ChatRoomService; // 記得 import 這個
import com.ntou.auctionSite.dto.ChatRoom.ChatRoomDto;
import com.ntou.auctionSite.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "聊天功能", description = "聊天室相關 API")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    // 👇 1. 必須加上這一行，讓 Spring 注入 ChatRoomService
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    @MessageMapping("/chat")
    @Operation(summary = "發送即時訊息（WebSocket）")
    public void processMessage(@Payload Message chatMessage) {
        Message savedMsg = chatMessageService.save(chatMessage);

        // 修正後的推播邏輯 (改用 Topic 避免權限問題)
        messagingTemplate.convertAndSend(
                "/topic/user/" + chatMessage.getRecipientId(),
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    @Operation(summary = "查詢聊天歷史")
    public ResponseEntity<List<Message>> findChatMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId
    ) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    // 👇 新增的 API
    @GetMapping("/chat-rooms/{userId}")
    @Operation(summary = "查詢使用者的所有聊天室列表")
    public ResponseEntity<List<ChatRoomDto>> getUserChatRooms(@PathVariable String userId) {
        // 先取得使用者的所有聊天室 (原始資料)
        List<ChatRoom> rooms = chatRoomService.findUserChatRooms(userId);

        // 將原始資料轉換成 DTO，並填入對方名字
        List<ChatRoomDto> roomDtos = rooms.stream().map(room -> {
            // 因為我們現在只查 findBySenderId，所以 Sender 是我自己，Recipient 永遠是對方
            String otherUserId = room.getRecipientId();

            // 去資料庫查對方名字 (如果查不到就顯示 "未知用戶")
            String otherUserName = userRepository.findById(otherUserId)
                    .map(user -> user.getUsername()) // 假設 User 物件有 getUsername()
                    .orElse("未知用戶");

            return new ChatRoomDto(room.getChatId(), otherUserId, otherUserName);
        }).toList();

        return ResponseEntity.ok(roomDtos);
    }
}