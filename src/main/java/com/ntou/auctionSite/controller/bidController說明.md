postman測試:
createAuction:
POST:
http://localhost:8080/createAucs/P104?price=878&time=2025-11-19T10:30:00

getAllAuctionProduct:
GET:
http://localhost:8080/auctions/

placeBid
POST:
http://localhost:8080/bids/P105?price=150&bidderId=buyer002

terminateAuction
PUT:
http://localhost:8080/P105/terminate

createOrder
POST:
http://localhost:8080/orders/P105