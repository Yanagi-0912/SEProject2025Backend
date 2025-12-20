package com.ntou.auctionSite.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * 應用程式啟動器
 * 在應用程式啟動時執行資料初始化和遷移任務
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Runner implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("========== 應用程式啟動初始化 ==========");

        // 執行資料遷移：為舊使用者設定 remainingDrawTimes 預設值
        migrateUserDrawTimes();

        log.info("========== 初始化完成 ==========");
    }

    /**
     * 資料遷移：為沒有 remainingDrawTimes 欄位的舊使用者設定預設值
     * 使用 MongoTemplate 直接操作資料庫，避免 @Builder.Default 的干擾
     */
    private void migrateUserDrawTimes() {
        try {
            log.info("開始檢查使用者抽獎次數欄位...");

            // 使用 MongoDB 查詢：找出所有沒有 remainingDrawTimes 欄位的文檔
            Query query = new Query(Criteria.where("remainingDrawTimes").exists(false));

            // 批次更新：為所有符合條件的文檔設定 remainingDrawTimes = 10
            Update update = new Update().set("remainingDrawTimes", 10);

            // 執行批次更新
            var result = mongoTemplate.updateMulti(query, update, "users");

            long updatedCount = result.getModifiedCount();

            if (updatedCount > 0) {
                log.info("✅ 資料遷移完成：已為 {} 位舊使用者設定抽獎次數預設值", updatedCount);
            } else {
                log.info("✅ 所有使用者都已有抽獎次數欄位，無需遷移");
            }

        } catch (Exception e) {
            log.error("❌ 資料遷移失敗：{}", e.getMessage(), e);
            // 不拋出異常，允許應用程式繼續啟動
        }
    }
}
