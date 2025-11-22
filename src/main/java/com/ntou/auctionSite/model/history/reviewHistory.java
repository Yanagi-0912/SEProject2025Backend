package com.ntou.auctionSite.model.history;

import java.time.LocalDateTime;

public class reviewHistory extends History {
    private final String reviewID;
    private final String productID;
    private final String actionType; // CREATE or EDIT

    public reviewHistory(String userID, String reviewID, String productID, String actionType) {
        super(userID);
        this.reviewID = reviewID;
        this.productID = productID;
        this.actionType = actionType;
    }

    public String getReviewID() {return reviewID;}
    public String getProductID() {return productID;}
    public String getActionType() {return actionType;}

    @Override
    public String toString() {
        return "reviewHistory [Action=" + actionType
                + ", ReviewID=" + reviewID
                + ", ProductID=" + productID
                + ", UserID=" + getUserID()
                + ", TimeStamp=" + getTimeStamp() + "]";
    }
}
