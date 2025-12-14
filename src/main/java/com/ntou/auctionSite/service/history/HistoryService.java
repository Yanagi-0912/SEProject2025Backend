package com.ntou.auctionSite.service.history;

import com.ntou.auctionSite.model.history.*;
import com.ntou.auctionSite.repository.history.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository<History> historyRepository;

    @Autowired
    private BidHistoryRepository bidHistoryRepository;

    @Autowired
    private BrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private ReviewHistoryRepository reviewHistoryRepository;

    // ===== 通用 History 操作 =====

    public List<History> getAllHistoriesByUserId(String userId) {
        return historyRepository.findAllByUserID(userId);
    }

    public Optional<History> getHistoryById(String historyId) {
        return historyRepository.findByHistoryID(historyId);
    }

    public History saveHistory(History history) {
        return historyRepository.save(history);
    }

    public void deleteHistory(String historyId) {
        historyRepository.deleteById(historyId);
    }

    // ===== BidHistory 操作 =====

    public List<bidHistory> getBidHistoriesByUserId(String userId) {
        return bidHistoryRepository.findByUserID(userId);
    }

    public List<bidHistory> getBidHistoriesByProductId(String productId) {
        return bidHistoryRepository.findByProductID(productId);
    }

    public List<bidHistory> searchBidHistory(String userId, String productId) {
        return bidHistoryRepository.findByUserIDAndProductID(userId, productId);
    }

    public bidHistory saveBidHistory(bidHistory history) {
        return bidHistoryRepository.save(history);
    }

    // ===== BrowseHistory 操作 =====

    public List<browseHistory> getBrowseHistoriesByUserId(String userId) {
        return browseHistoryRepository.findByUserID(userId);
    }

    public List<browseHistory> getBrowseHistoriesByProductId(String productId) {
        return browseHistoryRepository.findByProductID(productId);
    }

    public List<browseHistory> searchBrowseHistory(String userId, String productId) {
        return browseHistoryRepository.findByUserIDAndProductID(userId, productId);
    }

    public browseHistory saveBrowseHistory(browseHistory history) {
        return browseHistoryRepository.save(history);
    }

    // ===== PurchaseHistory 操作 =====

    public List<purchaseHistory> getPurchaseHistoriesByUserId(String userId) {
        return purchaseHistoryRepository.findByUserID(userId);
    }

    public List<purchaseHistory> getPurchaseHistoriesByProductId(String productId) {
        return purchaseHistoryRepository.findByProductIDContaining(productId);
    }

    public purchaseHistory savePurchaseHistory(purchaseHistory history) {
        return purchaseHistoryRepository.save(history);
    }

    // ===== ReviewHistory 操作 =====

    public List<reviewHistory> getReviewHistoriesByUserId(String userId) {
        return reviewHistoryRepository.findByUserID(userId);
    }

    public List<reviewHistory> getReviewHistoriesByReviewId(String reviewId) {
        return reviewHistoryRepository.findByReviewID(reviewId);
    }

    public List<reviewHistory> getReviewHistoriesByActionType(String actionType) {
        return reviewHistoryRepository.findByActionType(actionType);
    }

    public List<reviewHistory> getReviewHistoriesByUserIdAndActionType(String userId, String actionType) {
        return reviewHistoryRepository.findByUserIDAndActionType(userId, actionType);
    }

    public reviewHistory saveReviewHistory(reviewHistory history) {
        return reviewHistoryRepository.save(history);
    }

    // ===== 綜合搜尋功能 =====

    /**
     * 根據商品 ID 搜尋所有類型的歷史記錄
     */
    public List<History> searchAllHistoriesByProductId(String productId) {
        List<History> allHistories = new ArrayList<>();
        allHistories.addAll(bidHistoryRepository.findByProductID(productId));
        allHistories.addAll(browseHistoryRepository.findByProductID(productId));
        allHistories.addAll(purchaseHistoryRepository.findByProductIDContaining(productId));
        return allHistories;
    }

    /**
     * 根據使用者 ID 搜尋所有類型的歷史記錄
     */
    public List<History> searchAllHistoriesByUserId(String userId) {
        List<History> allHistories = new ArrayList<>();
        allHistories.addAll(bidHistoryRepository.findByUserID(userId));
        allHistories.addAll(browseHistoryRepository.findByUserID(userId));
        allHistories.addAll(purchaseHistoryRepository.findByUserID(userId));
        allHistories.addAll(reviewHistoryRepository.findByUserID(userId));
        return allHistories;
    }
}

