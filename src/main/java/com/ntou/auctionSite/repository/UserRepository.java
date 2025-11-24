package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Optional:可能有值也可能沒值 強制做例外處理
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
     boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}
