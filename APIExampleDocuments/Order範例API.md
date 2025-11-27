先加入商品:
http://localhost:8080/api/cart/items
```json
{
  "productId":"PROD4C5BCAF2",
  "quantity":6
}
```
新增訂單:
http://localhost:8080/api/orders/add
body:
```json
{
  "buyerID": "thugcreeper",
  "orderType": "DIRECT",
  "cart": {
    "items": [
      {
        "productId": "PROD4C5BCAF2",
        "quantity": 6
      }
    ]
  }
}



```
查看訂單:
http://localhost:8080/api/orders/57E62C26-3