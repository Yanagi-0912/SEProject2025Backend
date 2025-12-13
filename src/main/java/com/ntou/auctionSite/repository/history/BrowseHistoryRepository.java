package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.browseHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrowseHistoryRepository extends HistoryRepository {
    // 覆寫父類方法，返回更具體的類型
    @Override
    @Query("{ 'UserID': ?0 }")
    List<browseHistory> findByUserID(String userId);

    @Query("{ 'ProductID': ?0 }")
    List<browseHistory> findByProductID(String productId);

    @Query("{ 'UserID': ?0, 'ProductID': ?1 }")
    List<browseHistory> findByUserIDAndProductID(String userId, String productId);
}

