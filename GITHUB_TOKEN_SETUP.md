# GitHub Token 設置指南

## 問題描述
圖片上傳 API 返回 `401 UNAUTHORIZED` 錯誤，表示 GitHub Personal Access Token (PAT) 無效或過期。

## 解決方案

### 步驟 1: 訪問 GitHub Settings
1. 訪問 https://github.com/settings/tokens
2. 登入你的 GitHub 帳戶

### 步驟 2: 檢查現有 Token
- 查看 "Personal access tokens (classic)" 或 "Fine-grained personal access tokens"
- 檢查現有 token 是否過期
  - 如果過期日期已過，需要生成新的 token

### 步驟 3: 生成新的 Token (如果需要)

**方法 A: 使用 Classic Token (推薦)**
1. 點擊 "Generate new token (classic)"
2. 輸入 Token 名稱，例如: `SEProject2025ImageUpload`
3. 設定過期日期（推薦：90 天或以上）
4. **選擇權限**:
   - ✅ `repo` - 完全控制私有倉庫 (推薦)
   - 或 `public_repo` - 只控制公開倉庫
5. 點擊 "Generate token"
6. **複製 token** - 重要！只會顯示一次

### 步驟 4: 更新應用程式配置

編輯 `src/main/resources/application-dev.yml`:

```yaml
github:
  token: <你的新token>              # 將 <你的新token> 替換為剛複製的 token
  owner: <你的GitHub用戶名>        # 例如: Yanagi-0912
  repo: <你的倉庫名>               # 例如: SEProject2025imageCDN
  branch: main                    # 分支名稱
  base-path: images               # 圖片存放路徑
```

### 步驟 5: 重新啟動應用程式

```bash
# 停止當前運行的應用
# 重新啟動 Spring Boot 應用
mvn spring-boot:run
```

## 驗證 Token 有效性

上傳一張圖片後，檢查：
1. 圖片是否成功上傳到 GitHub 倉庫
2. 是否取得有效的 CDN URL
3. 應用日誌是否顯示 "圖片上傳成功"

## 常見錯誤

### 401 Unauthorized
- Token 無效或過期
- Token 格式錯誤（複製時可能包含空格）
- Token 不是有效的 GitHub token

**解決方案**: 重新生成新 token 並確保完整複製

### 403 Forbidden
- Token 權限不足
- Token 未包含 `repo` 權限

**解決方案**: 編輯 token 添加 `repo` 權限

### 422 Unprocessable Entity
- 檔案已存在
- 分支名稱不正確

**解決方案**: 應用會自動嘗試更新檔案

## 安全建議

1. **永遠不要提交真實 token** 到 Git 版本控制
2. 定期檢查 token 的過期日期
3. 對於生產環境，使用環境變數存儲 token:
   ```yaml
   github:
     token: ${GITHUB_TOKEN}
     owner: ${GITHUB_OWNER}
     repo: ${GITHUB_REPO}
   ```
4. 使用較短的過期時間（如 90 天）並定期更新

## 更多幫助

- GitHub 官方文檔: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
- GitHub API: https://docs.github.com/en/rest

## 錯誤響應示例

當上傳失敗時，API 會返回詳細的錯誤信息：

```json
{
  "error": "GitHub token 無效或過期！\n詳情: ...",
  "type": "GITHUB_AUTH_ERROR",
  "solution": [
    "訪問 https://github.com/settings/tokens",
    "檢查 Personal Access Token (PAT) 是否過期",
    "生成新的 token（選擇 'repo' 或 'public_repo' 權限）",
    "更新 application-dev.yml 中的 github.token",
    "重新啟動應用程式"
  ]
}
```

