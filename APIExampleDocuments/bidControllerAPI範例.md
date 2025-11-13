# 拍賣 API 文件

## 創建拍賣
- **方法:** POST  
- **URL:** `http://localhost:8080/createAucs/P104?price=878&time=2025-11-19T10:30:00`

## 取得所有拍賣商品
- **方法:** GET  
- **URL:** `http://localhost:8080/auctions/`

## 出價
- **方法:** POST  
- **URL:** `http://localhost:8080/bids/P105?price=150&bidderId=buyer002`

## 結束拍賣
- **方法:** PUT  
- **URL:** `http://localhost:8080/P105/terminate`

## 創建訂單
- **方法:** POST  
- **URL:** `http://localhost:8080/orders/P105`
