package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class reviewHistory extends History {
    @Getter
    @JsonProperty("reviewID")
    private final String reviewID;
    @Getter
    @JsonProperty("actionType")
    private final String actionType; // CREATE or EDIT

    // 無參數建構子（供 Jackson 和 Spring Data MongoDB 使用）
    public reviewHistory() {
        super();
        this.reviewID = null;
        this.actionType = null;
    }

    // 用於 POST 建立（自動產生 historyID）
    public reviewHistory(String userID, String reviewID, String actionType) {
        super(userID);
        this.reviewID = reviewID;
        this.actionType = actionType;
    }

    // 用於從 MongoDB 讀取
    @JsonCreator
    public reviewHistory(
            @JsonProperty(value = "_id") String historyID,
            @JsonProperty("userID") String userID,
            @JsonProperty("timeStamp") java.time.LocalDateTime timeStamp,
            @JsonProperty("reviewID") String reviewID,
            @JsonProperty("actionType") String actionType) {
        super(historyID, userID, timeStamp);
        this.reviewID = reviewID;
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "reviewHistory [Action=" + actionType
                + ", ReviewID=" + reviewID
                + ", UserID=" + super.getUserID()
                + ", TimeStamp=" + super.getTimeStamp() + "]";
    }
}
