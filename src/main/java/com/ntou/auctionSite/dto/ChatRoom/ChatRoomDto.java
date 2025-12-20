package com.ntou.auctionSite.dto.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private String roomId;       // 聊天室 ID
    private String otherUserId;  // 對方 ID
    private String otherUserName;// ✅ 對方真實名稱 (我們要顯示這個)
}
