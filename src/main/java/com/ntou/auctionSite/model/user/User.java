package com.ntou.auctionSite.model.user;

import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.history.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // 使用者實作 UserDetails 介面以支援 Spring Security

    @Id
    private String id;              // 使用者 ID

    @Indexed(unique = true)
    private String userName;        // 使用者名稱

    @Indexed(unique = true)
    private String email;           // 電子郵件

    private String password;        // 密碼

    // 注意：購物車資料存放在獨立的 Cart collection 中，不在 User 物件內
    // 透過 CartService 來操作購物車

    private String userNickname;    // 使用者暱稱

    private String address;         // 地址

    private String phoneNumber;     // 電話號碼

    private Float averageRating;    //賣家評分

    private Integer ratingCount;    //評分人數

    private Boolean isBanned;       //是否被封鎖

     @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    // 還沒有用到的欄位
    private ArrayList<browseHistory> browseHistoryArrayList;
    private ArrayList<purchaseHistory> purchaseHistoryArrayList;
    private ArrayList<bidHistory> bidHistoryArrayList;
    private ArrayList<Product> ownedProducts;
    private ArrayList<Product> favoriteList;

}
