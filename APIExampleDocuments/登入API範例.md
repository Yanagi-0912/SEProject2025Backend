## 註冊:
- **方法:** POST
- **URL:** http://localhost:8080/api/auth/register
- **Body (JSON):** 
```json
{
"username":"7bb1c",
"password":"1234asdasd",
"email":"12345@email"
}
```

## 登入:
- **方法:** POST
- **URL:** http://localhost:8080/api/auth/login
```json
{
"username":"7bb1c",
"password":"1234asdasd",

}
```

會拿到一個token，複製後去測試別的API
- **方法:**GET:
- **URL:**http://localhost:8080/products/
header key選authorization value填入Bearer <token>
然後請求應該就行了
