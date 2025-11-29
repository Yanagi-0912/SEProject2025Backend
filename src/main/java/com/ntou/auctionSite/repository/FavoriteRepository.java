package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.user.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 收藏清單 Repository
 * 負責 Favorite collection 的資料庫操作
 */
@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {

    /**
     * 根據使用者 ID 查詢收藏清單
     * 一個使用者只有一個收藏清單（一對一關係）
     */
    Optional<Favorite> findByUserId(String userId);

    /**
     * 刪除使用者的收藏清單
     */
    void deleteByUserId(String userId);
}

