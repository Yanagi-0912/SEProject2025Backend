package com.ntou.auctionSite.model.history;

//日期時間
import java.time.LocalDateTime;

public class History {
    final private LocalDateTime TimeStamp;
    final private String UserID;
    public History(String userID) {
        this.UserID = userID;
        this.TimeStamp = LocalDateTime.now();
    }
    public LocalDateTime getTimeStamp() {
        return TimeStamp;
    }
    public String getUserID() {
        return UserID;
    }
}
