package com.ntou.auctionSite.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class reviewHistory extends History {
    private final String reviewID;
    private final String actionType; // CREATE or EDIT

    @JsonCreator
    public reviewHistory(
            @JsonProperty("userID") String userID,
            @JsonProperty("reviewID") String reviewID,
            @JsonProperty("actionType") String actionType) {
        super(userID);
        this.reviewID = reviewID;
        this.actionType = actionType;
    }

    public String getReviewID() {return reviewID;}
    public String getActionType() {return actionType;}
}
