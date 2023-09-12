package com.hoangdao.seleniumcrawler.demo.repository;

import com.hoangdao.seleniumcrawler.demo.entity.Magazine;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MagazineRepository extends MongoRepository<Magazine, String> {
}
