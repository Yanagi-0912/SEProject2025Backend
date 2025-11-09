# SEProject2025
NTOU software engineering lesson project 海大拍賣系統

## 環境配置

### 本地開發

1. 複製 `application-dev.yml.example` 為 `application-dev.yml`:
```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
```

2. 編輯 `application-dev.yml` 填入你的配置

3. 在 IDE 或命令列設定使用 dev profile:
```bash
# 使用 Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 使用 Java
java -jar target/auctionSite-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

或在 IntelliJ IDEA 中:
- Run -> Edit Configurations
- Active profiles: `dev`

### Render 部署

在 Render Dashboard 設定以下環境變數:

| 變數名稱 | 說明 | 範例 |
|---------|------|------|
| `MONGODB_URI` | MongoDB 連線字串 | `mongodb+srv://user:pass@cluster...` |
| `MONGODB_DATABASE` | 資料庫名稱 | `mongodb` |
| `JWT_SECRET` | JWT 密鑰 | 至少 256 bits |
| `JWT_EXPIRATION` | Token 有效期 (ms) | `86400000` (24小時) |
| `CORS_ALLOWED_ORIGINS` | 允許的前端網址 | `https://your-frontend.vercel.app` |

## API 端點

### 認證相關
- `POST /api/auth/register` - 用戶註冊
- `POST /api/auth/login` - 用戶登入

### 商品相關
- `GET /api/products/` - 查看所有商品
- `GET /api/products/{id}` - 查看商品詳情
- `POST /api/products/add` - 新增商品 (需要驗證)
- `PUT /api/products/edit/{id}` - 編輯商品 (需要驗證)
- `DELETE /api/products/delete/{id}` - 刪除商品 (需要驗證)

## CORS 配置

前端網址已配置允許跨域請求:
- 生產環境: `https://se-project2025-frontend-8qpw.vercel.app`
- 開發環境: `http://localhost:3000`

