# 商品 API 文件

## 創建商品
- **方法:** POST  
- **URL:** http://localhost:8080/products/add  
- **Body (JSON):** 
```json
{
  "sellerID": "P003",
  "productName": "三星S26",
  "productPrice": 7000,
  "productType": "DIRECT",
  "productCategory": "電子裝置與配件",
  "productStock": 590,
  "productStatus": "ACTIVE",
  "productDescription": "旗艦智慧手機，螢幕亮眼，效能頂尖。",
  "productImage": "https://example.com/cookie.jpg",
  "averageRating": 0,
  "reviewCount": 0,# 商品 API 文件
```
## 創建商品
- **方法:** POST  
- **URL:** http://localhost:8080/products/add  
- **Body (JSON):** 
```json
{
  "sellerID": "P003",
  "productName": "三星S26",
  "productPrice": 7000,
  "productType": "DIRECT",
  "productCategory": "電子裝置與配件",
  "productStock": 590,
  "productStatus": "ACTIVE",
  "productDescription": "旗艦智慧手機，螢幕亮眼，效能頂尖。",
  "productImage": "https://example.com/cookie.jpg",
  "averageRating": 0,
  "reviewCount": 0,
  "totalSales": 0,
  "viewCount": 0,
  "nowHighestBid": 0
}
```

## 取得商品列表（分頁） 
- **方法:** GET 
- **URL:** http://localhost:8080/products/? page=1&pageSize=10 

## 取得單一商品 
- **方法:** GET 
- **URL:** http://localhost:8080/products/P001

## 編輯商品 
- **方法:** PUT 
- **URL:** http://localhost:8080/products/edit/P001 
 ```json 
{ 
  "sellerID": "S001", 
  "productName": "餅乾(升級版)", 
  "productPrice": 120, 
  "productType": "DIRECT", 
  "productCategory": "食品", 
  "productStock": 60, "productStatus": "ACTIVE" 
} ```
## 上架商品 
- **方法:**PUT
- **URL:** http://localhost:8080/products/upload/P001將

## 下架商品 
- **方法:** PUT
- **URL:** http://localhost:8080/products/withdraw/P001 

## 刪除商品
- **方法:**  DELETE 
- **URL:** http://localhost:8080/products/delete/P001 
