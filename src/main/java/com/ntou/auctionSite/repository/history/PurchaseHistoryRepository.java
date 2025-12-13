package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.purchaseHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends HistoryRepository {
    // 覆寫父類方法，返回更具體的類型
    @Override
    @Query("{ 'UserID': ?0 }")
    List<purchaseHistory> findByUserID(String userId);

    // 搜尋包含特定商品 ID 的購買紀錄（購買歷史可能包含多個商品）
    @Query("{ 'ProductID': ?0 }")
    List<purchaseHistory> findByProductIDContaining(String productId);
}

