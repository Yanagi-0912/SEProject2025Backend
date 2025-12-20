# 聊天功能流程完整說明

## 🎯 核心概念

你的聊天系統已經**完整實作**了發送、接收、儲存訊息的功能！

### `processMessage` 的三合一功能：
1. ✅ **接收**訊息（從前端 WebSocket）
2. ✅ **儲存**訊息（到 MongoDB）
3. ✅ **推送**訊息（給接收者）

---

## 📡 完整通訊流程

### 場景：用戶 A (ID=1) 發送訊息給用戶 B (ID=2)

```
┌─────────────────┐                    ┌─────────────────┐                    ┌─────────────────┐
│   用戶 A (1)    │                    │   後端伺服器     │                    │   用戶 B (2)    │
└─────────────────┘                    └─────────────────┘                    └─────────────────┘
         │                                      │                                      │
         │ 1️⃣ 連線到 ws://localhost:8080/ws   │                                      │
         │─────────────────────────────────────>│                                      │
         │         連線成功！                    │                                      │
         │                                      │ 2️⃣ 連線到 ws://localhost:8080/ws   │
         │                                      │<─────────────────────────────────────│
         │                                      │         連線成功！                    │
         │                                      │                                      │
         │ 3️⃣ 訂閱: /user/1/queue/messages    │                                      │
         │─────────────────────────────────────>│                                      │
         │         訂閱成功！                    │                                      │
         │                                      │ 4️⃣ 訂閱: /user/2/queue/messages    │
         │                                      │<─────────────────────────────────────│
         │                                      │         訂閱成功！                    │
         │                                      │                                      │
         │ 5️⃣ 發送訊息到 /app/chat              │                                      │
         │ {                                    │                                      │
         │   senderId: 1,                       │                                      │
         │   recipientId: 2,                    │                                      │
         │   content: "你好"                    │                                      │
         │ }                                    │                                      │
         │─────────────────────────────────────>│                                      │
         │                                      │                                      │
         │              💾 processMessage() 執行 │                                      │
         │              ├─ 儲存到 MongoDB       │                                      │
         │              └─ 推送通知              │                                      │
         │                                      │                                      │
         │                                      │ 6️⃣ 推送到 /user/2/queue/messages    │
         │                                      │ {                                    │
         │                                      │   id: "msg123",                      │
         │                                      │   senderId: 1,                       │
         │                                      │   recipientId: 2,                    │
         │                                      │   content: "你好"                    │
         │                                      │ }                                    │
         │                                      │─────────────────────────────────────>│
         │                                      │               7️⃣ 即時收到訊息！      │
         │                                      │                                      │
```

---

## 🔧 後端已實作的兩個主要功能

### 1. `processMessage` - 發送訊息（WebSocket）

**觸發方式**: 前端透過 WebSocket 發送到 `/app/chat`

**對應註解**: `@MessageMapping("/chat")`

**做的事情**:
```java
public void processMessage(@Payload Message chatMessage) {
    // ✅ 第一步：儲存訊息到資料庫
    Message savedMsg = chatMessageService.save(chatMessage);
    
    // ✅ 第二步：即時推送給接收者
    messagingTemplate.convertAndSendToUser(
        String.valueOf(chatMessage.getRecipientId()),  // 接收者 ID
        "/queue/messages",                              // 訊息佇列
        new ChatNotification(...)                       // 通知內容
    );
}
```

**前端呼叫方式**:
```javascript
// 前端發送訊息
client.publish({
  destination: '/app/chat',  // ← 會進入 processMessage
  body: JSON.stringify({
    senderId: 1,
    recipientId: 2,
    content: "你好"
  })
});
```

---

### 2. `findChatMessages` - 查詢歷史訊息（REST API）

**觸發方式**: 前端透過 HTTP GET 請求

**API 路徑**: `GET /api/messages/{senderId}/{recipientId}`

**做的事情**:
```java
public ResponseEntity<List<Message>> findChatMessages(
    @PathVariable Long senderId,
    @PathVariable Long recipientId
) {
    // 從資料庫查詢兩個用戶之間的所有歷史訊息
    return ResponseEntity.ok(
        chatMessageService.findChatMessages(senderId, recipientId)
    );
}
```

**前端呼叫方式**:
```javascript
// 前端查詢歷史訊息
fetch('http://localhost:8080/api/messages/1/2')
  .then(res => res.json())
  .then(messages => {
    console.log('歷史訊息:', messages);
  });
```

---

## 🎬 實際使用場景

### 場景 1: 用戶 A 發送訊息給用戶 B

#### 前端 A 的操作：
```javascript
// 發送訊息
chatService.sendMessage(2, "你好，請問商品還有嗎？");
```

#### 後端自動執行：
1. `processMessage` 接收訊息
2. 儲存到 MongoDB
3. 推送通知到用戶 B 的頻道

#### 前端 B 自動接收：
```javascript
// B 的訂閱回調會自動觸發
onMessageReceived(notification) {
  console.log('收到新訊息:', notification.content);
  // 輸出: "收到新訊息: 你好，請問商品還有嗎？"
}
```

---

### 場景 2: 用戶 B 查看與 A 的聊天記錄

#### 前端 B 的操作：
```javascript
// 載入歷史訊息
const messages = await getChatHistory(2, 1);
console.log(messages);
```

#### 輸出結果：
```json
[
  {
    "id": "msg001",
    "chatId": "1_2",
    "senderId": 1,
    "recipientId": 2,
    "content": "你好，請問商品還有嗎？",
    "timestamp": "2025-12-19T10:30:00"
  },
  {
    "id": "msg002",
    "chatId": "1_2",
    "senderId": 2,
    "recipientId": 1,
    "content": "有的，還有 5 件庫存",
    "timestamp": "2025-12-19T10:31:00"
  }
]
```

---

## 🔑 關鍵概念解釋

### 為什麼沒有"單獨的傳訊息 API"？

因為 **WebSocket 本身就是即時傳輸**！

傳統 REST API 的做法：
```
前端 → POST /api/sendMessage → 後端儲存 → 前端輪詢 → 取得新訊息
```

WebSocket 的做法（更高效）：
```
前端 → WebSocket /app/chat → 後端儲存 + 即時推送 → 接收者立即收到
```

### `@MessageMapping("/chat")` vs `@GetMapping("/messages/...")`

| 特性 | @MessageMapping | @GetMapping |
|------|----------------|-------------|
| 協議 | WebSocket (STOMP) | HTTP |
| 用途 | **即時發送訊息** | 查詢歷史訊息 |
| 觸發方式 | `client.publish()` | `fetch()` 或 `axios.get()` |
| 即時性 | ✅ 即時推送 | ❌ 需要主動查詢 |
| 是否儲存 | ✅ 會儲存 | N/A (只讀取) |

---

## 📱 前端完整實作範例

```javascript
class ChatManager {
  constructor(userId) {
    this.userId = userId;
    this.client = null;
  }

  // 1️⃣ 初始化：連線 + 訂閱
  async init(recipientId) {
    // 載入歷史訊息
    await this.loadHistory(recipientId);
    
    // 建立 WebSocket 連線
    this.connect();
  }

  // 2️⃣ 載入歷史訊息（REST API）
  async loadHistory(recipientId) {
    const response = await fetch(
      `http://localhost:8080/api/messages/${this.userId}/${recipientId}`
    );
    const messages = await response.json();
    this.displayMessages(messages);
  }

  // 3️⃣ 建立 WebSocket 連線
  connect() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      
      onConnect: () => {
        console.log('✅ WebSocket 已連線');
        
        // 訂閱個人訊息頻道
        this.client.subscribe(
          `/user/${this.userId}/queue/messages`,
          (message) => {
            const notification = JSON.parse(message.body);
            this.onNewMessage(notification);
          }
        );
      }
    });

    this.client.activate();
  }

  // 4️⃣ 發送訊息（觸發 processMessage）
  sendMessage(recipientId, content) {
    this.client.publish({
      destination: '/app/chat',  // ← 對應到 @MessageMapping("/chat")
      body: JSON.stringify({
        senderId: this.userId,
        recipientId: recipientId,
        content: content
      })
    });
  }

  // 5️⃣ 接收新訊息
  onNewMessage(notification) {
    console.log('📬 收到新訊息:', notification.content);
    this.displayMessage(notification);
  }

  // 6️⃣ 顯示訊息
  displayMessages(messages) {
    messages.forEach(msg => this.displayMessage(msg));
  }

  displayMessage(message) {
    // 更新 UI 顯示訊息
    console.log(`[${message.senderId}]: ${message.content}`);
  }
}

// 使用範例
const chatManager = new ChatManager(1);  // 當前用戶 ID = 1
chatManager.init(2);                     // 與用戶 2 聊天

// 發送訊息
chatManager.sendMessage(2, "你好！");
```

---

## ✅ 總結

你的後端聊天功能是**完整的**！包含：

| 功能 | 方法 | 協議 | 用途 |
|------|------|------|------|
| ✅ 發送訊息 | `processMessage` | WebSocket | 接收 + 儲存 + 推送 |
| ✅ 接收訊息 | `processMessage` | WebSocket | 自動推送給接收者 |
| ✅ 儲存訊息 | `processMessage` | WebSocket | 存入 MongoDB |
| ✅ 查詢歷史 | `findChatMessages` | HTTP | 查詢聊天記錄 |

### 為什麼容易混淆？

因為 `processMessage` **一個方法做了三件事**：
1. 接收訊息（從發送者）
2. 儲存訊息（到資料庫）
3. 推送訊息（給接收者）

這就是為什麼它叫 "process"（處理）而不是 "send"（發送）！

### 前端需要做的事：

1. **連線到 WebSocket**: `new SockJS('http://localhost:8080/ws')`
2. **訂閱個人頻道**: `/user/{userId}/queue/messages`
3. **發送訊息**: `client.publish({ destination: '/app/chat', body: ... })`
4. **查詢歷史**: `fetch('/api/messages/{senderId}/{recipientId}')`

就這麼簡單！🎉

