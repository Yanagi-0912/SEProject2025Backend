package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.Message;
import com.ntou.auctionSite.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ChatMessageService 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("聊天訊息服務測試")
class ChatMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private Message testMessage;
    private String chatId;

    @BeforeEach
    void setUp() {
        chatId = "507f1f77bcf86cd799439011_507f1f77bcf86cd799439012";

        testMessage = Message.builder()
                .senderId("507f1f77bcf86cd799439011")
                .recipientId("507f1f77bcf86cd799439012")
                .content("你好，這個商品還有庫存嗎？")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("測試：儲存訊息時應該設定正確的 chatId")
    void save_ShouldSetCorrectChatId() {
        // Arrange
        when(chatRoomService.getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", true))
                .thenReturn(Optional.of(chatId));
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(invocation -> {
                    Message msg = invocation.getArgument(0);
                    msg.setId("msg-123");
                    return msg;
                });

        // Act
        Message savedMessage = chatMessageService.save(testMessage);

        // Assert
        assertNotNull(savedMessage, "儲存的訊息不應為 null");
        assertEquals(chatId, savedMessage.getChatId(), "chatId 應該被正確設定");
        assertEquals("msg-123", savedMessage.getId(), "訊息 ID 應該被設定");

        verify(chatRoomService, times(1)).getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", true);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("測試：儲存訊息時，如果取得 chatId 失敗應該拋出例外")
    void save_WhenGetChatIdFails_ShouldThrowException() {
        // Arrange
        when(chatRoomService.getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", true))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class,
                () -> chatMessageService.save(testMessage),
                "當無法取得 chatId 時應該拋出例外");

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("測試：查詢聊天訊息，聊天室存在時應該返回訊息列表")
    void findChatMessages_WhenChatRoomExists_ShouldReturnMessages() {
        // Arrange
        List<Message> expectedMessages = Arrays.asList(
                Message.builder()
                        .id("msg1")
                        .chatId(chatId)
                        .senderId("507f1f77bcf86cd799439011")
                        .recipientId("507f1f77bcf86cd799439012")
                        .content("訊息1")
                        .timestamp(LocalDateTime.now().minusMinutes(5))
                        .build(),
                Message.builder()
                        .id("msg2")
                        .chatId(chatId)
                        .senderId("507f1f77bcf86cd799439012")
                        .recipientId("507f1f77bcf86cd799439011")
                        .content("訊息2")
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        when(chatRoomService.getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", false))
                .thenReturn(Optional.of(chatId));
        when(messageRepository.findByChatId(chatId))
                .thenReturn(expectedMessages);

        // Act
        List<Message> result = chatMessageService.findChatMessages("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012");

        // Assert
        assertNotNull(result, "結果不應為 null");
        assertEquals(2, result.size(), "應該返回 2 則訊息");
        assertEquals("msg1", result.get(0).getId());
        assertEquals("msg2", result.get(1).getId());

        verify(chatRoomService, times(1)).getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", false);
        verify(messageRepository, times(1)).findByChatId(chatId);
    }

    @Test
    @DisplayName("測試：查詢聊天訊息，聊天室不存在時應該返回空列表")
    void findChatMessages_WhenChatRoomNotExists_ShouldReturnEmptyList() {
        // Arrange
        when(chatRoomService.getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", false))
                .thenReturn(Optional.empty());

        // Act
        List<Message> result = chatMessageService.findChatMessages("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012");

        // Assert
        assertNotNull(result, "結果不應為 null");
        assertTrue(result.isEmpty(), "應該返回空列表");

        verify(chatRoomService, times(1)).getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", false);
        verify(messageRepository, never()).findByChatId(anyString());
    }

    @Test
    @DisplayName("測試：儲存訊息後，訊息內容應該保持不變")
    void save_ShouldPreserveMessageContent() {
        // Arrange
        String originalContent = "測試訊息內容";
        String originalSenderId = "507f1f77bcf86cd799439011";
        String originalRecipientId = "507f1f77bcf86cd799439012";

        testMessage.setContent(originalContent);
        testMessage.setSenderId(originalSenderId);
        testMessage.setRecipientId(originalRecipientId);

        when(chatRoomService.getChatId(originalSenderId, originalRecipientId, true))
                .thenReturn(Optional.of(chatId));
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Message savedMessage = chatMessageService.save(testMessage);

        // Assert
        assertEquals(originalContent, savedMessage.getContent(), "訊息內容應該保持不變");
        assertEquals(originalSenderId, savedMessage.getSenderId(), "發送者 ID 應該保持不變");
        assertEquals(originalRecipientId, savedMessage.getRecipientId(), "接收者 ID 應該保持不變");
    }

    @Test
    @DisplayName("測試：查詢空的聊天記錄")
    void findChatMessages_WhenNoMessages_ShouldReturnEmptyList() {
        // Arrange
        when(chatRoomService.getChatId("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012", false))
                .thenReturn(Optional.of(chatId));
        when(messageRepository.findByChatId(chatId))
                .thenReturn(new ArrayList<>());

        // Act
        List<Message> result = chatMessageService.findChatMessages("507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "沒有訊息時應該返回空列表");
    }
}

