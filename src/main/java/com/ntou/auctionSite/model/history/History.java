package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.UUID;

public class History {
    @Id
    @Getter
    @JsonProperty("_id")
    private String historyID;
    @Getter
    @JsonProperty("userID")
    private String userID;
    @Getter
    @JsonProperty("timeStamp")
    private LocalDateTime timeStamp;

    // 無參數建構子（供 Jackson 和 Spring Data MongoDB 使用）
    public History() {
        this.historyID = UUID.randomUUID().toString();
        this.userID = null;
        this.timeStamp = LocalDateTime.now();
    }

    // 用於 POST 建立歷史記錄（自動產生 historyID）
    public History(String userID) {
        this.historyID = UUID.randomUUID().toString();
        this.userID = userID;
        this.timeStamp = LocalDateTime.now();
    }

    // 用於子類別的 @JsonCreator 建構子（從 MongoDB 讀取時）
    protected History(String historyID, String userID, LocalDateTime timeStamp) {
        this.historyID = historyID != null ? historyID : UUID.randomUUID().toString();
        this.userID = userID;
        this.timeStamp = timeStamp != null ? timeStamp : LocalDateTime.now();
    }

    public String getHistoryID() {
        return historyID;
    }

    public String getUserID() {
        return userID;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
