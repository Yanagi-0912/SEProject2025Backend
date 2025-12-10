package com.ntou.auctionSite.model.coupon;

public enum CouponType {
    PERCENT,//打XX折,0-1的float
    FIXED,//折抵XX元
    FREESHIP,//免運券，可考慮設定低消
    BUY_ONE_GET_ONE,//買一送一
}
