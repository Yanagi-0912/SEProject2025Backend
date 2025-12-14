package com.ntou.auctionSite.repository.history;

import com.ntou.auctionSite.model.history.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository<T extends History> extends MongoRepository<T,String> {
    // 查詢使用者的所有歷史記錄（返回 List）
    @Query("{ 'UserID': ?0 }")
    List<T> findByUserID(String userId);

    // 根據 HistoryID 查詢單一歷史記錄（返回 Optional）
    Optional<T> findByHistoryID(String historyID);

    // 查詢使用者的所有歷史記錄（別名方法）
    List<T> findAllByUserID(String userId);
}

