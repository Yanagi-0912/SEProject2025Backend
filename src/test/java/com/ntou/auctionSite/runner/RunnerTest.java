package com.ntou.auctionSite.runner;

import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.UserRepository;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Runner 資料遷移測試
 * 測試舊使用者自動設定 remainingDrawTimes 的功能
 */
@SpringBootTest
@DisplayName("資料遷移測試 (Runner)")
class RunnerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Runner runner;

    private String testOldUserId;
    private String testNewUserId;

    @BeforeEach
    void setUp() {
        // 清理測試資料
        userRepository.findByUserName("oldUserTest").ifPresent(userRepository::delete);
        userRepository.findByUserName("newUserTest").ifPresent(userRepository::delete);

        // 使用 MongoTemplate 直接插入沒有 remainingDrawTimes 欄位的文檔
        // 這樣可以繞過 @Builder.Default 的影響，真正模擬舊資料
        Document oldUserDoc = new Document()
                .append("userName", "oldUserTest")
                .append("email", "old@test.com")
                .append("password", "password")
                .append("_class", "com.ntou.auctionSite.model.user.User");
        mongoTemplate.insert(oldUserDoc, "users");

        // 取得插入的 ID
        testOldUserId = oldUserDoc.getObjectId("_id").toString();

        // 建立已有 remainingDrawTimes 的使用者（正常方式）
        User testNewUser = User.builder()
                .userName("newUserTest")
                .email("new@test.com")
                .password("password")
                .remainingDrawTimes(5)
                .build();
        testNewUser = userRepository.save(testNewUser);
        testNewUserId = testNewUser.getId();
    }

    @AfterEach
    void tearDown() {
        // 清理測試資料
        userRepository.findByUserName("oldUserTest").ifPresent(userRepository::delete);
        userRepository.findByUserName("newUserTest").ifPresent(userRepository::delete);
    }

    @Test
    @DisplayName("資料遷移 - 應該為沒有 remainingDrawTimes 的舊使用者設定預設值")
    void migration_ShouldSetDefaultDrawTimesForOldUsers() throws Exception {
        // Arrange - 確認舊使用者沒有 remainingDrawTimes 欄位（直接查詢 MongoDB）
        Document oldUserDoc = mongoTemplate.findById(testOldUserId, Document.class, "users");
        assertNotNull(oldUserDoc, "舊使用者應該存在");
        assertFalse(oldUserDoc.containsKey("remainingDrawTimes"), "舊使用者不應該有 remainingDrawTimes 欄位");

        // 確認新使用者已有 remainingDrawTimes
        User newUserBefore = userRepository.findByUserName("newUserTest").orElseThrow();
        assertEquals(5, newUserBefore.getRemainingDrawTimes(), "新使用者應該有設定的抽獎次數");

        // Act - 執行遷移
        ApplicationArguments args = mock(ApplicationArguments.class);
        runner.run(args);

        // Assert - 驗證舊使用者被更新（直接查詢 MongoDB）
        Document oldUserDocAfter = mongoTemplate.findById(testOldUserId, Document.class, "users");
        assertNotNull(oldUserDocAfter, "遷移後舊使用者應該仍然存在");
        assertTrue(oldUserDocAfter.containsKey("remainingDrawTimes"), "遷移後舊使用者應該有 remainingDrawTimes 欄位");
        assertEquals(10, oldUserDocAfter.getInteger("remainingDrawTimes"), "舊使用者應該被設定為預設值 10");

        // 驗證新使用者不被改變
        User newUserAfter = userRepository.findByUserName("newUserTest").orElseThrow();
        assertEquals(5, newUserAfter.getRemainingDrawTimes(), "新使用者的抽獎次數不應該被改變");

        System.out.println("✅ 資料遷移測試通過");
        System.out.println("   舊使用者抽獎次數: (無欄位) -> " + oldUserDocAfter.getInteger("remainingDrawTimes"));
        System.out.println("   新使用者抽獎次數: " + newUserBefore.getRemainingDrawTimes() + " -> " + newUserAfter.getRemainingDrawTimes());
    }

    @Test
    @DisplayName("資料遷移 - 多次執行應該是冪等的（不會重複更新）")
    void migration_ShouldBeIdempotent() throws Exception {
        // Arrange
        ApplicationArguments args = mock(ApplicationArguments.class);

        // Act - 執行遷移兩次
        runner.run(args);
        Document userDocAfterFirstRun = mongoTemplate.findById(testOldUserId, Document.class, "users");
        int drawTimesAfterFirstRun = userDocAfterFirstRun.getInteger("remainingDrawTimes");

        runner.run(args);
        Document userDocAfterSecondRun = mongoTemplate.findById(testOldUserId, Document.class, "users");
        int drawTimesAfterSecondRun = userDocAfterSecondRun.getInteger("remainingDrawTimes");

        // Assert - 兩次執行結果應該相同
        assertEquals(10, drawTimesAfterFirstRun, "第一次遷移應該設定為 10");
        assertEquals(10, drawTimesAfterSecondRun, "第二次遷移不應該改變值");
        assertEquals(drawTimesAfterFirstRun, drawTimesAfterSecondRun, "多次遷移應該產生相同結果");

        System.out.println("✅ 冪等性測試通過：多次執行遷移不會影響資料");
    }

    @Test
    @DisplayName("新註冊使用者 - 應該自動有 remainingDrawTimes")
    void newUser_ShouldHaveDefaultDrawTimes() {
        // Arrange & Act - 使用 Builder 建立新使用者
        User newUser = User.builder()
                .userName("brandNewUser")
                .email("brandnew@test.com")
                .password("password")
                .build();

        // Assert - 新使用者應該有預設值（透過 @Builder.Default）
        assertNotNull(newUser.getRemainingDrawTimes(), "新建使用者應該有預設抽獎次數");
        assertEquals(10, newUser.getRemainingDrawTimes(), "預設值應該是 10");

        System.out.println("✅ 新使用者預設值測試通過");
    }
}

