package com.ntou.auctionSite.service;

import com.ntou.auctionSite.model.ChatRoom;
import com.ntou.auctionSite.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ChatRoomService 單元測試
 * 使用 Mockito 模擬 Repository，測試 Service 層的邏輯
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("聊天室服務測試")
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private Long senderId;
    private Long recipientId;
    private String expectedChatId;
    private ChatRoom mockChatRoom;

    @BeforeEach
    void setUp() {
        // 準備測試資料
        senderId = 1L;
        recipientId = 2L;
        expectedChatId = "1_2";

        mockChatRoom = ChatRoom.builder()
                .id("test-id-123")
                .chatId(expectedChatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
    }

    @Test
    @DisplayName("測試：查詢已存在的聊天室，應該返回現有的 chatId")
    void getChatId_WhenChatRoomExists_ShouldReturnExistingChatId() {
        // Arrange (準備)
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.of(mockChatRoom));

        // Act (執行)
        Optional<String> result = chatRoomService.getChatId(senderId, recipientId, false);

        // Assert (驗證)
        assertTrue(result.isPresent(), "應該返回 chatId");
        assertEquals(expectedChatId, result.get(), "chatId 應該正確");

        // 驗證只呼叫了查詢，沒有呼叫儲存
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("測試：聊天室不存在且不建立新聊天室，應該返回 empty")
    void getChatId_WhenChatRoomNotExistsAndDontCreate_ShouldReturnEmpty() {
        // Arrange
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());

        // Act
        Optional<String> result = chatRoomService.getChatId(senderId, recipientId, false);

        // Assert
        assertFalse(result.isPresent(), "應該返回 empty");
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("測試：聊天室不存在且需要建立，應該建立雙向聊天室記錄")
    void getChatId_WhenChatRoomNotExistsAndCreate_ShouldCreateBidirectionalRooms() {
        // Arrange
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());

        // Mock save 方法返回儲存的物件
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<String> result = chatRoomService.getChatId(senderId, recipientId, true);

        // Assert
        assertTrue(result.isPresent(), "應該返回新建立的 chatId");
        assertEquals(expectedChatId, result.get(), "chatId 格式應該正確");

        // 驗證呼叫了查詢
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);

        // 驗證儲存了兩次（雙向記錄）
        verify(chatRoomRepository, times(2)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("測試：建立聊天室時，應該儲存正確的雙向記錄")
    void getChatId_WhenCreatingRoom_ShouldSaveCorrectBidirectionalData() {
        // Arrange
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());

        // 捕獲儲存的 ChatRoom 物件
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        chatRoomService.getChatId(senderId, recipientId, true);

        // Assert - 使用 ArgumentCaptor 驗證儲存的資料
        verify(chatRoomRepository, times(2)).save(argThat(chatRoom -> {
            // 驗證 chatId 都是 "1_2"
            assertEquals(expectedChatId, chatRoom.getChatId());

            // 驗證包含正向和反向記錄
            boolean isForward = chatRoom.getSenderId().equals(senderId)
                             && chatRoom.getRecipientId().equals(recipientId);
            boolean isBackward = chatRoom.getSenderId().equals(recipientId)
                              && chatRoom.getRecipientId().equals(senderId);

            return isForward || isBackward;
        }));
    }

    @Test
    @DisplayName("測試：chatId 格式應該正確（senderId_recipientId）")
    void getChatId_ShouldHaveCorrectFormat() {
        // Arrange
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<String> result = chatRoomService.getChatId(senderId, recipientId, true);

        // Assert
        assertTrue(result.isPresent());
        String chatId = result.get();
        assertTrue(chatId.matches("\\d+_\\d+"), "chatId 應該符合 數字_數字 格式");
        assertEquals("1_2", chatId);
    }

    @Test
    @DisplayName("測試：使用不同的用戶 ID 組合")
    void getChatId_WithDifferentUserIds_ShouldWorkCorrectly() {
        // Arrange
        Long sender = 100L;
        Long recipient = 200L;
        String expectedId = "100_200";

        when(chatRoomRepository.findBySenderIdAndRecipientId(sender, recipient))
                .thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<String> result = chatRoomService.getChatId(sender, recipient, true);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedId, result.get());
    }
}

