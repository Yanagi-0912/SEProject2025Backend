package com.ntou.auctionSite.model.history;

import java.time.LocalDateTime;

public class reviewHistory extends History {
    private final String reviewID;
    private final String actionType; // CREATE or EDIT

    public reviewHistory(String userID, String reviewID, String actionType) {
        super(userID);
        this.reviewID = reviewID;
        this.actionType = actionType;
    }

    public String getReviewID() {return reviewID;}
    public String getActionType() {return actionType;}

    @Override
    public String toString() {
        return "reviewHistory [Action=" + actionType
                + ", ReviewID=" + reviewID
                + ", UserID=" + getUserID()
                + ", TimeStamp=" + getTimeStamp() + "]";
    }
}
