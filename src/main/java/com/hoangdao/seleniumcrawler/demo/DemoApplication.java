package com.hoangdao.seleniumcrawler.demo;

import com.hoangdao.seleniumcrawler.demo.entity.Magazine;
import com.hoangdao.seleniumcrawler.demo.service.MagazineCrawlRequest;
import com.hoangdao.seleniumcrawler.demo.service.CourtCrawlerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class);
//        CourtCrawlerService service = CourtCrawlerService.getInstance();
////        service.craw();
//        Magazine magazine = service.crawMagazine(
//                new MagazineCrawlRequest(
//                        "test", "test",
//                        "https://fullstack.edu.vn/", null
//                )
//        );
//        System.out.println(magazine);
    }

}
