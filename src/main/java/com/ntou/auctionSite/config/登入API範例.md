註冊:POST
http://localhost:8080/api/auth/register
body:
{
"username":"7bb1c",
"password":"1234asdasd",
"email":"12345@email"
}

登入:POST
http://localhost:8080/api/auth/login
body:
{
"username":"7bb1c",
"password":"1234asdasd"
}
會拿到一個token，複製後去測試別的API
GET:
http://localhost:8080/products/
header key選authorization value填入Bearer <token>
然後請求應該就行了
