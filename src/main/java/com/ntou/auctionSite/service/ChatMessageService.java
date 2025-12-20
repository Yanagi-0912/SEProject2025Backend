package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.Message;
import com.ntou.auctionSite.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final MessageRepository repository;
    private final ChatRoomService chatRoomService;

    // 儲存聊天訊息
    public Message save(Message chatMessage) {
        var chatId = chatRoomService
                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow(() -> new RuntimeException("Failed to get or create chat ID"));
        chatMessage.setChatId(chatId);
        chatMessage.setTimestamp(LocalDateTime.now());
        repository.save(chatMessage);
        return chatMessage;
    }

    // 根據發送者和接收者 ID 查詢聊天訊息
    public List<Message> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatId(senderId, recipientId, false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
}

