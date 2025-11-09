# Render 部署指南

## 步驟 1: 準備 Render 帳號

1. 前往 [Render.com](https://render.com/)
2. 使用 GitHub 帳號登入

## 步驟 2: 創建新的 Web Service

1. 點擊 "New +" → "Web Service"
2. 連接你的 GitHub Repository: `SEProject2025Backend`
3. 設定以下資訊:
   - **Name**: `seproject2025-backend` (或你想要的名稱)
   - **Region**: `Singapore` (選擇離台灣最近的)
   - **Branch**: `main` (或你的主分支名稱)
   - **Runtime**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/auctionSite-0.0.1-SNAPSHOT.jar`

## 步驟 3: 設定環境變數

在 "Environment" 標籤中添加以下環境變數:

| Key | Value |
|-----|-------|
| `MONGODB_URI` | `mongodb+srv://york4818_db_user:tXh29M4XztpsStcL@se2025.cgya0at.mongodb.net/?retryWrites=true&w=majority&appName=SE2025` |
| `MONGODB_DATABASE` | `mongodb` |
| `JWT_SECRET` | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` |
| `JWT_EXPIRATION` | `86400000` |
| `CORS_ALLOWED_ORIGINS` | `https://se-project2025-frontend-8qpw.vercel.app` |

**注意**: Render 會自動設定 `PORT` 環境變數,不需要手動設定。

## 步驟 4: 部署

1. 點擊 "Create Web Service"
2. 等待建置和部署完成 (首次可能需要 5-10 分鐘)
3. 部署完成後,會得到一個網址,例如: `https://seproject2025-backend.onrender.com`

## 步驟 5: 更新前端 API 網址

將 Render 給的網址更新到前端專案的 API 設定中。

## 步驟 6: 測試

使用以下命令測試 API 是否正常運作:

```bash
# 測試健康檢查 (如果有的話)
curl https://your-backend-url.onrender.com/api/auth/login

# 測試註冊
curl -X POST https://your-backend-url.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123","email":"test@example.com"}'
```

## 常見問題

### 1. 部署失敗
- 檢查 Logs 查看錯誤訊息
- 確認 Java 版本是否正確 (需要 Java 21)

### 2. 啟動後一段時間無法訪問
- Render Free Plan 會在 15 分鐘無活動後進入休眠
- 第一次訪問需要等待約 30 秒喚醒

### 3. CORS 錯誤
- 確認 `CORS_ALLOWED_ORIGINS` 環境變數設定正確
- 確認前端網址完全匹配 (包括 https://)

## 自動部署

Render 會自動監聽 GitHub repository 的變更:
- 每次 push 到 `main` 分支會自動重新部署
- 可以在 Render Dashboard 查看部署歷史和 logs

## 監控

在 Render Dashboard 可以查看:
- **Logs**: 應用程式運行日誌
- **Metrics**: CPU/Memory 使用情況
- **Events**: 部署歷史
ㄉˋ