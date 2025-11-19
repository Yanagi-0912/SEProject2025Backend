package com.ntou.auctionSite.model;

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
public class User implements UserDetails {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userName;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role = Role.USER;  // 新增這行

    private Cart cart = new Cart();

    // 實作 UserDetails (這裡有空要回來檢視一下有沒有必要 因為後來沒有分買家賣家)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
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

    // 拍賣網站欄位
    private String userNickname;
    private String address;
    private String phoneNumber;
    private float averageRating;
    private int ratingCount;
    private boolean isBanned;

    private ArrayList<browseHistory> browseHistoryArrayList;
    private ArrayList<purchaseHistory> purchaseHistoryArrayList;
    private ArrayList<bidHistory> bidHistoryArrayList;
    private ArrayList<Product> ownedProducts;
    private ArrayList<Product> favoriteList;

}
