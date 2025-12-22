package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.bidHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidHistoryRepository extends HistoryRepository<bidHistory> {
    // 繼承自父介面的 findByUserID 方法會自動回傳 List<bidHistory>

    @Query("{ 'productID': ?0 }")
    List<bidHistory> findByProductID(String productId);

    @Query("{ 'userID': ?0, 'productID': ?1 }")
    List<bidHistory> findByUserIDAndProductID(String userId, String productId);
}
