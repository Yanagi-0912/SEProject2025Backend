package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.ChatRoom;
import com.ntou.auctionSite.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatId(String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        // 1. 嘗試查詢現有房間 (先不管方向，只要這兩人在聊就算)
        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(!createNewRoomIfNotExists) {
                        return Optional.empty();
                    }

                    String chatId;
                    if (senderId.compareTo(recipientId) < 0) {
                        chatId = String.format("%s_%s", senderId, recipientId);
                    } else {
                        chatId = String.format("%s_%s", recipientId, senderId);
                    }

                    // 2. 建立雙向關係 (A->B 和 B->A 都存，但共用同一個 chatId)
                    // 這樣前端查列表時，兩邊都能看到對方

                    ChatRoom senderRecipient = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientSender = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();

                    chatRoomRepository.save(senderRecipient);
                    // 防止自己跟自己聊天時存兩次
                    if (!senderId.equals(recipientId)) {
                        chatRoomRepository.save(recipientSender);
                    }

                    return Optional.of(chatId);
                });
    }

    public java.util.List<ChatRoom> findUserChatRooms(String userId) {
        return chatRoomRepository.findBySenderId(userId);
    }
}