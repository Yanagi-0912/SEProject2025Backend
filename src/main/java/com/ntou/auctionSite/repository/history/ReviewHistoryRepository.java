package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.reviewHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewHistoryRepository extends HistoryRepository {
    // 覆寫父類方法，返回更具體的類型
    @Override
    @Query("{ 'UserID': ?0 }")
    List<reviewHistory> findByUserID(String userId);

    @Query("{ 'reviewID': ?0 }")
    List<reviewHistory> findByReviewID(String reviewId);

    @Query("{ 'actionType': ?0 }")
    List<reviewHistory> findByActionType(String actionType);

    @Query("{ 'UserID': ?0, 'actionType': ?1 }")
    List<reviewHistory> findByUserIDAndActionType(String userId, String actionType);
}

