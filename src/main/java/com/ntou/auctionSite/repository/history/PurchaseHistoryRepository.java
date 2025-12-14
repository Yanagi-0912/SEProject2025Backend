package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.purchaseHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends HistoryRepository<purchaseHistory> {
    // 繼承自父介面的 findByUserID 方法會自動回傳 List<purchaseHistory>

    // 搜尋包含特定商品 ID 的購買紀錄（購買歷史可能包含多個商品）
    @Query("{ 'ProductID': ?0 }")
    List<purchaseHistory> findByProductIDContaining(String productId);
}

