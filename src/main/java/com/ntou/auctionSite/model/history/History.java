package com.ntou.auctionSite.model.history;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

public class History {
    @Id
    final private String HistoryID;
    final private String UserID;
    @Getter
    final private LocalDateTime TimeStamp;

    public History(String userID) {
        this.HistoryID = java.util.UUID.randomUUID().toString();
        this.UserID = userID;
        this.TimeStamp = LocalDateTime.now();
    }

    public String getHistoryID() { return HistoryID; }
    public String getUserID() { return UserID; }
}
