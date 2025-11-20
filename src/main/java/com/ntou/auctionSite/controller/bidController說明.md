postman測試:
createAuction:
POST:
http://localhost:8080/api/createAucs/P104?price=878&time=2025-11-19T10:30:00

getAllAuctionProduct:
GET:
http://localhost:8080/api/auctions/

placeBid
POST:
http://localhost:8080/api/bids/P105?price=150&bidderId=buyer002

terminateAuction
PUT:
http://localhost:8080/api/P105/terminate

