package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.browseHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrowseHistoryRepository extends HistoryRepository<browseHistory> {
    // 繼承自父介面的 findByUserID 方法會自動回傳 List<browseHistory>

    @Query("{ 'productID': ?0 }")
    List<browseHistory> findByProductID(String productId);

    @Query("{ 'userID': ?0, 'productID': ?1 }")
    List<browseHistory> findByUserIDAndProductID(String userId, String productId);
}
