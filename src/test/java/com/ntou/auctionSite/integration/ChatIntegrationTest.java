package com.ntou.auctionSite.integration;

import com.ntou.auctionSite.model.ChatRoom;
import com.ntou.auctionSite.model.Message;
import com.ntou.auctionSite.repository.ChatRoomRepository;
import com.ntou.auctionSite.repository.MessageRepository;
import com.ntou.auctionSite.service.ChatMessageService;
import com.ntou.auctionSite.service.ChatRoomService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 聊天功能整合測試
 * 測試完整的聊天流程，包含 Service 和 Repository 的協作
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("聊天功能整合測試")
class ChatIntegrationTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    private static final Long SENDER_ID = 9999L;
    private static final Long RECIPIENT_ID = 8888L;
    private static String testChatId;

    @BeforeEach
    void setUp() {
        // 清理測試資料
        chatRoomRepository.findBySenderIdAndRecipientId(SENDER_ID, RECIPIENT_ID)
                .ifPresent(chatRoomRepository::delete);
        chatRoomRepository.findBySenderIdAndRecipientId(RECIPIENT_ID, SENDER_ID)
                .ifPresent(chatRoomRepository::delete);
    }

    @AfterEach
    void tearDown() {
        // 清理測試資料
        if (testChatId != null) {
            messageRepository.findByChatId(testChatId).forEach(messageRepository::delete);
        }
        chatRoomRepository.findBySenderIdAndRecipientId(SENDER_ID, RECIPIENT_ID)
                .ifPresent(chatRoomRepository::delete);
        chatRoomRepository.findBySenderIdAndRecipientId(RECIPIENT_ID, SENDER_ID)
                .ifPresent(chatRoomRepository::delete);
    }

    @Test
    @Order(1)
    @DisplayName("整合測試：首次對話應該建立聊天室和雙向記錄")
    void firstConversation_ShouldCreateChatRoomAndBidirectionalRecords() {
        // Act - 首次取得 chatId（會建立新聊天室）
        Optional<String> chatIdOpt = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, true);

        // Assert
        assertTrue(chatIdOpt.isPresent(), "應該成功建立 chatId");
        testChatId = chatIdOpt.get();

        // 驗證正向記錄存在
        Optional<ChatRoom> forwardRoom = chatRoomRepository
                .findBySenderIdAndRecipientId(SENDER_ID, RECIPIENT_ID);
        assertTrue(forwardRoom.isPresent(), "應該存在正向聊天室記錄");
        assertEquals(testChatId, forwardRoom.get().getChatId());

        // 驗證反向記錄存在
        Optional<ChatRoom> backwardRoom = chatRoomRepository
                .findBySenderIdAndRecipientId(RECIPIENT_ID, SENDER_ID);
        assertTrue(backwardRoom.isPresent(), "應該存在反向聊天室記錄");
        assertEquals(testChatId, backwardRoom.get().getChatId());

        System.out.println("✅ 測試通過：成功建立聊天室，chatId = " + testChatId);
    }

    @Test
    @Order(2)
    @DisplayName("整合測試：完整的發送和接收訊息流程")
    void completeMessageFlow_ShouldWork() {
        // Step 1: 建立聊天室
        Optional<String> chatIdOpt = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, true);
        assertTrue(chatIdOpt.isPresent());
        testChatId = chatIdOpt.get();

        // Step 2: 用戶 A 發送第一則訊息
        Message message1 = Message.builder()
                .senderId(SENDER_ID)
                .recipientId(RECIPIENT_ID)
                .content("你好，這個商品還有庫存嗎？")
                .timestamp(LocalDateTime.now())
                .build();

        Message savedMsg1 = chatMessageService.save(message1);
        assertNotNull(savedMsg1.getId(), "訊息應該被儲存並取得 ID");
        assertEquals(testChatId, savedMsg1.getChatId(), "訊息應該關聯到正確的聊天室");

        // Step 3: 用戶 B 發送回覆
        Message message2 = Message.builder()
                .senderId(RECIPIENT_ID)
                .recipientId(SENDER_ID)
                .content("有的！目前還有 5 件庫存")
                .timestamp(LocalDateTime.now())
                .build();

        Message savedMsg2 = chatMessageService.save(message2);
        assertNotNull(savedMsg2.getId());
        assertEquals(testChatId, savedMsg2.getChatId());

        // Step 4: 查詢聊天記錄（從用戶 A 的角度）
        List<Message> messagesFromA = chatMessageService.findChatMessages(SENDER_ID, RECIPIENT_ID);
        assertEquals(2, messagesFromA.size(), "應該有 2 則訊息");

        // Step 5: 查詢聊天記錄（從用戶 B 的角度）
        List<Message> messagesFromB = chatMessageService.findChatMessages(RECIPIENT_ID, SENDER_ID);
        assertEquals(2, messagesFromB.size(), "從任一方查詢都應該看到相同的訊息");

        // Step 6: 驗證訊息內容
        assertTrue(messagesFromA.stream()
                .anyMatch(m -> m.getContent().contains("還有庫存嗎")),
                "應該包含用戶 A 的訊息");
        assertTrue(messagesFromA.stream()
                .anyMatch(m -> m.getContent().contains("有的")),
                "應該包含用戶 B 的回覆");

        System.out.println("✅ 測試通過：完整的對話流程運作正常");
        System.out.println("   訊息數量：" + messagesFromA.size());
    }

    @Test
    @Order(3)
    @DisplayName("整合測試：重複取得相同聊天室 ID 不應該重複建立")
    void getChatIdMultipleTimes_ShouldNotDuplicateRooms() {
        // Act - 多次取得相同的 chatId
        Optional<String> chatId1 = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, true);
        Optional<String> chatId2 = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, true);
        Optional<String> chatId3 = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, true);

        // Assert
        assertTrue(chatId1.isPresent());
        assertTrue(chatId2.isPresent());
        assertTrue(chatId3.isPresent());

        testChatId = chatId1.get();

        // 所有 chatId 應該相同
        assertEquals(chatId1.get(), chatId2.get());
        assertEquals(chatId2.get(), chatId3.get());

        // 驗證資料庫中只有兩筆記錄（正向和反向）
        List<ChatRoom> allRooms = chatRoomRepository.findAll();
        long count = allRooms.stream()
                .filter(room -> room.getChatId().equals(testChatId))
                .count();

        assertEquals(2, count, "應該只有兩筆聊天室記錄（正向和反向）");

        System.out.println("✅ 測試通過：不會重複建立聊天室");
    }

    @Test
    @Order(4)
    @DisplayName("整合測試：查詢不存在的聊天記錄應該返回空列表")
    void findNonExistentChat_ShouldReturnEmptyList() {
        // Act - 查詢從未對話過的兩個用戶
        List<Message> messages = chatMessageService.findChatMessages(SENDER_ID, RECIPIENT_ID);

        // Assert
        assertNotNull(messages, "不應該返回 null");
        assertTrue(messages.isEmpty(), "應該返回空列表");

        System.out.println("✅ 測試通過：查詢不存在的對話返回空列表");
    }

    @Test
    @Order(5)
    @DisplayName("整合測試：多則連續訊息應該保持順序")
    void multipleMessages_ShouldMaintainOrder() {
        // Step 1: 建立聊天室
        Optional<String> chatIdOpt = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, true);
        assertTrue(chatIdOpt.isPresent());
        testChatId = chatIdOpt.get();

        // Step 2: 發送多則訊息
        for (int i = 1; i <= 5; i++) {
            Message message = Message.builder()
                    .senderId(SENDER_ID)
                    .recipientId(RECIPIENT_ID)
                    .content("測試訊息 " + i)
                    .timestamp(LocalDateTime.now().plusSeconds(i))
                    .build();
            chatMessageService.save(message);

            // 稍微延遲確保時間戳不同
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Step 3: 查詢訊息
        List<Message> messages = chatMessageService.findChatMessages(SENDER_ID, RECIPIENT_ID);

        // Assert
        assertEquals(5, messages.size(), "應該有 5 則訊息");

        // 驗證訊息內容
        for (int i = 0; i < messages.size(); i++) {
            assertTrue(messages.get(i).getContent().contains("測試訊息"),
                    "訊息內容應該正確");
        }

        System.out.println("✅ 測試通過：多則訊息儲存正常");
        messages.forEach(m -> System.out.println("   - " + m.getContent()));
    }

    @Test
    @Order(6)
    @DisplayName("整合測試：聊天室 ID 格式應該正確")
    void chatId_ShouldHaveCorrectFormat() {
        // Act
        Optional<String> chatIdOpt = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, true);

        // Assert
        assertTrue(chatIdOpt.isPresent());
        testChatId = chatIdOpt.get();

        // 驗證格式：數字_數字
        assertTrue(testChatId.matches("\\d+_\\d+"),
                "chatId 應該符合 '數字_數字' 格式");

        // 驗證包含正確的 ID
        assertTrue(testChatId.contains(SENDER_ID.toString())
                || testChatId.contains(RECIPIENT_ID.toString()),
                "chatId 應該包含用戶 ID");

        System.out.println("✅ 測試通過：chatId 格式正確 = " + testChatId);
    }
}

