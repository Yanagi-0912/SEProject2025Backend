package com.ntou.auctionSite;

import com.ntou.auctionSite.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AuctionSiteApplication {
	@Autowired
	private ProductRepository repository;
	public static void main(String[] args) {
		SpringApplication.run(AuctionSiteApplication.class, args);
	}

}
