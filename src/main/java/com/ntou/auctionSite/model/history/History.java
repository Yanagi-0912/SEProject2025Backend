package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@JsonIgnoreProperties(value = {"historyID", "timeStamp"}, allowGetters = true)
public class History {
    @Id
    private final String historyID;
    private final String userID;
    @Getter
    private final LocalDateTime timeStamp;

    public History(String userID) {
        this.historyID = java.util.UUID.randomUUID().toString();
        this.userID = userID;
        this.timeStamp = LocalDateTime.now();
    }

    public String getHistoryID() { return historyID; }
    public String getUserID() { return userID; }
}
