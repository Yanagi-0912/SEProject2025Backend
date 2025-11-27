package com.ntou.auctionSite.repository;

import com.ntou.auctionSite.model.history.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HistoryRepository extends MongoRepository<History,String> {

}
