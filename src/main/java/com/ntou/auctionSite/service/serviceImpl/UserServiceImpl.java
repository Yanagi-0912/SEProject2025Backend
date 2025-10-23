package com.ntou.auctionSite.service.serviceImpl;

import com.ntou.auctionSite.model.User;
import com.ntou.auctionSite.repository.LoginRepository;
import com.ntou.auctionSite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 登入功能
    @Override
    public User loginService(String id, String password) {
        Optional<User> opt = loginRepository.findById(id);
        if (opt.isEmpty()) return null;
        User user = opt.get();
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    //註冊功能
    @Override
    public void registerService(User user) {
        String raw = user.getPassword();
        user.setPassword(passwordEncoder.encode(raw));
        loginRepository.save(user);
    }
}
