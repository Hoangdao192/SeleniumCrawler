package com.hoangdao.seleniumcrawler.demo;

import java.util.List;

import com.hoangdao.seleniumcrawler.demo.entity.Magazine;
import com.hoangdao.seleniumcrawler.demo.service.MagazineCrawlRequest;
import com.hoangdao.seleniumcrawler.demo.service.SeleniumCrawlerService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class DemoApplication {

    public static void main(String[] args) {
        SeleniumCrawlerService service = SeleniumCrawlerService.getInstance();
//        service.craw();
        Magazine magazine = service.crawMagazine(
                new MagazineCrawlRequest(
                        "test", "test",
                        "https://fullstack.edu.vn/", null
                )
        );
        System.out.println(magazine);
    }

}
