# WebSocket 如何調用 processMessage 的完整機制

## 🎯 核心問題

**WebSocket 是怎麼調用 `processMessage` 的？**

答案：透過 **STOMP 協議** 和 **Spring 的訊息路由機制**！

---

## 📡 完整調用流程

### 第一步：建立基礎設施

#### 1. WebSocket 配置 (`WebSocketConfig.java`)

```java
@Configuration
@EnableWebSocketMessageBroker  // ← 關鍵：啟用 WebSocket 訊息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");  // 伺服器 → 客戶端
        config.setApplicationDestinationPrefixes("/app"); // 客戶端 → 伺服器
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")      // WebSocket 連線端點
                .setAllowedOriginPatterns("*")
                .withSockJS();           // 支援 SockJS 降級
    }
}
```

**這個配置做了什麼？**
- ✅ 註冊 WebSocket 端點：`/ws`
- ✅ 設定應用前綴：`/app`（所有發送到 `/app/**` 的訊息會被路由到 `@MessageMapping` 方法）
- ✅ 啟用訊息代理：`/queue` 和 `/topic`

---

#### 2. Controller 定義 (`ChatController.java`)

```java
@Controller  // ← 注意：不是 @RestController！
public class ChatController {

    @MessageMapping("/chat")  // ← 關鍵：處理 /app/chat 的訊息
    public void processMessage(@Payload Message chatMessage) {
        // 處理訊息
    }
}
```

**@MessageMapping("/chat") 的作用：**
- 監聽發送到 `/app/chat` 的 STOMP 訊息
- 自動將訊息內容轉換為 `Message` 物件
- 調用 `processMessage` 方法

---

### 第二步：前端發送訊息

```javascript
// 前端透過 STOMP 客戶端發送訊息
client.publish({
  destination: '/app/chat',  // ← 目標路徑
  body: JSON.stringify({
    senderId: 1,
    recipientId: 2,
    content: "你好"
  })
});
```

---

### 第三步：Spring 的路由魔法 ✨

```
前端發送訊息                     Spring 框架                     後端方法
     ↓                              ↓                              ↓
                                                            
1. client.publish()         
   destination: '/app/chat'
   body: { ... }
                          →  2. WebSocketConfig 攔截
                                - 看到前綴 /app
                                - 知道這是應用訊息
                                
                          →  3. Spring 訊息路由器
                                - 找到 @MessageMapping("/chat")
                                - 路徑匹配：/app + /chat = /app/chat ✅
                                
                          →  4. 訊息轉換器
                                - 將 JSON 轉換為 Message 物件
                                - 注入到 @Payload 參數
                                
                          →  5. 調用方法
                                     ↓
                               processMessage(chatMessage)
                               {
                                 // 儲存訊息
                                 // 推送通知
                               }
```

---

## 🔍 詳細解析

### 路徑匹配規則

| 前端發送目標 | WebSocketConfig 前綴 | @MessageMapping | 完整路徑 | 匹配結果 |
|-------------|---------------------|----------------|----------|---------|
| `/app/chat` | `/app` | `/chat` | `/app/chat` | ✅ 匹配 |
| `/app/hello` | `/app` | `/hello` | `/app/hello` | ✅ 匹配 |
| `/topic/xxx` | `/app` | `/chat` | `/app/chat` | ❌ 不匹配 |

**規則**：前端目標路徑 = 配置前綴 + MessageMapping 路徑

---

### 完整類比說明

#### 類比 1：REST API 的路由

```java
// REST API
@RestController
@RequestMapping("/api")  // ← 前綴
public class UserController {
    
    @GetMapping("/users")  // ← 路徑
    public List<User> getUsers() {
        // 處理 GET /api/users
    }
}
```

**前端調用**：
```javascript
fetch('http://localhost:8080/api/users')  // GET /api/users
```

---

#### 類比 2：WebSocket 的路由

```java
// WebSocket
@Controller
// 前綴在 WebSocketConfig 中設定：/app
public class ChatController {
    
    @MessageMapping("/chat")  // ← 路徑
    public void processMessage(@Payload Message msg) {
        // 處理發送到 /app/chat 的訊息
    }
}
```

**前端調用**：
```javascript
client.publish({
  destination: '/app/chat',  // 發送到 /app/chat
  body: JSON.stringify({ ... })
})
```

---

## 🎬 實際執行流程圖

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端 JavaScript                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 1. client.publish()
                              │    destination: '/app/chat'
                              │    body: '{"senderId":1, ...}'
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Spring WebSocket 層                           │
│  - 接收 WebSocket Frame                                         │
│  - 解析 STOMP 協議                                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 2. 檢查目標路徑
                              │    '/app/chat' 符合 '/app/**' 模式
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Spring 訊息路由層                             │
│  - 移除前綴 '/app'，剩下 '/chat'                                │
│  - 尋找 @MessageMapping("/chat")                                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 3. 找到對應方法
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Spring 訊息轉換層                             │
│  - 將 JSON body 轉換為 Message 物件                            │
│  - 使用 Jackson ObjectMapper                                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 4. 注入參數
                              │    @Payload Message chatMessage
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│              ChatController.processMessage()                     │
│                                                                  │
│  public void processMessage(@Payload Message chatMessage) {     │
│      // 5. 執行業務邏輯                                         │
│      Message savedMsg = chatMessageService.save(chatMessage);   │
│                                                                  │
│      // 6. 推送通知給接收者                                     │
│      messagingTemplate.convertAndSendToUser(...);               │
│  }                                                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 7. 透過 SimpMessagingTemplate
                              │    推送到 /user/2/queue/messages
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Spring 訊息代理層                             │
│  - 將訊息推送到指定用戶的訂閱頻道                                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 8. 透過 WebSocket 推送
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                  接收者的前端 JavaScript                         │
│  client.subscribe('/user/2/queue/messages', callback)           │
│  → callback 被觸發，收到訊息通知                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔑 關鍵概念

### 1. `@EnableWebSocketMessageBroker`

這個註解啟用了 Spring 的 WebSocket 訊息代理功能，它會：
- 處理 WebSocket 連線
- 解析 STOMP 協議
- 路由訊息到對應的 `@MessageMapping` 方法
- 管理訊息訂閱

### 2. `@MessageMapping`

類似於 `@GetMapping` 或 `@PostMapping`，但用於 WebSocket：

| REST API | WebSocket |
|----------|-----------|
| `@GetMapping("/users")` | `@MessageMapping("/chat")` |
| HTTP GET 請求觸發 | STOMP 訊息觸發 |
| 路徑：`/api/users` | 路徑：`/app/chat` |

### 3. `@Payload`

自動將 JSON 訊息內容轉換為 Java 物件：

```java
// 前端發送的 JSON
{
  "senderId": 1,
  "recipientId": 2,
  "content": "你好"
}

// Spring 自動轉換為
@Payload Message chatMessage
// chatMessage.getSenderId() = 1
// chatMessage.getRecipientId() = 2
// chatMessage.getContent() = "你好"
```

### 4. 路徑前綴的作用

**為什麼需要 `/app` 前綴？**

用來**區分訊息方向**：

| 前綴 | 方向 | 用途 | 範例 |
|------|------|------|------|
| `/app` | 客戶端 → 伺服器 | 發送訊息給應用 | `/app/chat` |
| `/queue` | 伺服器 → 客戶端（點對點） | 推送給特定用戶 | `/user/1/queue/messages` |
| `/topic` | 伺服器 → 客戶端（廣播） | 廣播給所有訂閱者 | `/topic/news` |

---

## 💻 程式碼對應關係

### 配置端點

```java
// WebSocketConfig.java
registry.addEndpoint("/ws")  // ← 連線端點
```

### 配置前綴

```java
// WebSocketConfig.java
config.setApplicationDestinationPrefixes("/app");  // ← 應用前綴
```

### 定義處理器

```java
// ChatController.java
@MessageMapping("/chat")  // ← 處理路徑
public void processMessage(@Payload Message chatMessage) {
    // ← 這裡會被調用！
}
```

### 前端發送

```javascript
// 前端
client.publish({
  destination: '/app/chat',  // ← 前綴 + 處理路徑
  body: JSON.stringify(message)
})
```

**完整對應**：
```
前端發送: /app/chat
         ↓
配置前綴: /app        ← WebSocketConfig
         ↓
路由匹配: /chat       ← @MessageMapping("/chat")
         ↓
方法調用: processMessage()
```

---

## 🆚 與 REST API 的對比

### REST API 調用方式

```java
@RestController
@RequestMapping("/api")
public class ChatController {
    
    @PostMapping("/send-message")  // HTTP POST /api/send-message
    public ResponseEntity<?> sendMessage(@RequestBody Message msg) {
        // 處理訊息
        return ResponseEntity.ok().build();
    }
}
```

```javascript
// 前端調用
fetch('http://localhost:8080/api/send-message', {
  method: 'POST',
  body: JSON.stringify(message)
})
```

**流程**：
```
HTTP Request → Spring MVC → @RequestMapping → @PostMapping → sendMessage()
```

---

### WebSocket 調用方式

```java
@Controller
public class ChatController {
    
    @MessageMapping("/chat")  // STOMP /app/chat
    public void processMessage(@Payload Message msg) {
        // 處理訊息
    }
}
```

```javascript
// 前端調用
client.publish({
  destination: '/app/chat',
  body: JSON.stringify(message)
})
```

**流程**：
```
WebSocket Frame → STOMP 解析 → Spring 訊息路由 → @MessageMapping → processMessage()
```

---

## 🧪 測試與驗證

### 方法 1：在 processMessage 中加入日誌

```java
@MessageMapping("/chat")
public void processMessage(@Payload Message chatMessage) {
    System.out.println("🔔 processMessage 被調用了！");
    System.out.println("發送者: " + chatMessage.getSenderId());
    System.out.println("接收者: " + chatMessage.getRecipientId());
    System.out.println("內容: " + chatMessage.getContent());
    
    // 原有邏輯
    Message savedMsg = chatMessageService.save(chatMessage);
    messagingTemplate.convertAndSendToUser(...);
}
```

### 方法 2：使用瀏覽器控制台測試

```javascript
// 1. 建立連線
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// 2. 連線成功後發送訊息
stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // 3. 發送訊息到 /app/chat
    stompClient.send('/app/chat', {}, JSON.stringify({
        senderId: 1,
        recipientId: 2,
        content: "測試訊息"
    }));
});
```

### 方法 3：查看 Spring 日誌

在 `application.yml` 中啟用 WebSocket 日誌：

```yaml
logging:
  level:
    org.springframework.messaging: DEBUG
    org.springframework.web.socket: DEBUG
```

你會看到類似的日誌：
```
Mapped [/chat] onto public void ChatController.processMessage(Message)
Processing message from destination [/app/chat]
Converted message body to type [Message]
Invoking method processMessage
```

---

## ✅ 總結

### WebSocket 調用 processMessage 的完整流程：

1. **前端發送**：`client.publish({ destination: '/app/chat', ... })`
2. **WebSocket 接收**：Spring WebSocket 層接收 Frame
3. **STOMP 解析**：解析 STOMP 協議，提取目標路徑和內容
4. **路徑匹配**：`/app/chat` 匹配到 `@MessageMapping("/chat")`
5. **訊息轉換**：JSON → Message 物件
6. **方法調用**：調用 `processMessage(@Payload Message chatMessage)`
7. **執行業務邏輯**：儲存訊息、推送通知

### 關鍵組件：

| 組件 | 作用 |
|------|------|
| `@EnableWebSocketMessageBroker` | 啟用 WebSocket 訊息處理 |
| `WebSocketConfig` | 配置端點和路徑前綴 |
| `@MessageMapping` | 定義訊息處理方法 |
| `@Payload` | 自動轉換訊息內容 |
| `SimpMessagingTemplate` | 發送訊息給客戶端 |

### 與 REST API 的類比：

| | REST API | WebSocket |
|---|----------|-----------|
| **啟用註解** | `@EnableWebMvc` | `@EnableWebSocketMessageBroker` |
| **方法註解** | `@GetMapping`, `@PostMapping` | `@MessageMapping` |
| **參數註解** | `@RequestBody` | `@Payload` |
| **路徑前綴** | `@RequestMapping("/api")` | `config.setApplicationDestinationPrefixes("/app")` |
| **調用方式** | HTTP Request | STOMP Message |

這就是 WebSocket 調用 `processMessage` 的完整機制！🎉

