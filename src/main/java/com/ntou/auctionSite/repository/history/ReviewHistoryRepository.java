package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.reviewHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewHistoryRepository extends HistoryRepository<reviewHistory> {
    // 繼承自父介面的 findByUserID 方法會自動回傳 List<reviewHistory>

    @Query("{ 'reviewID': ?0 }")
    List<reviewHistory> findByReviewID(String reviewId);

    @Query("{ 'actionType': ?0 }")
    List<reviewHistory> findByActionType(String actionType);

    @Query("{ 'userID': ?0, 'actionType': ?1 }")
    List<reviewHistory> findByUserIDAndActionType(String userId, String actionType);
}
