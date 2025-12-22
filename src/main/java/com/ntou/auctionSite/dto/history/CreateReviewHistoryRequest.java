package com.ntou.auctionSite.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateReviewHistoryRequest {
    @JsonProperty("reviewID")
    private String reviewID;

    @JsonProperty("actionType")
    private String actionType;

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}

