package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
    // 新增這行：找出我有參與的所有房間
    List<ChatRoom> findBySenderId(String senderId);
}

