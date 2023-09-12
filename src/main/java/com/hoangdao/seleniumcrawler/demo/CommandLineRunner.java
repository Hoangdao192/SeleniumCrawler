package com.hoangdao.seleniumcrawler.demo;

import com.hoangdao.seleniumcrawler.demo.service.MICCrawler;

public class CommandLineRunner {

    public static void main(String[] args) {
        MICCrawler crawler = new MICCrawler(
                "/home/hoangdao/Downloads/chrome/chromedriver-linux64/chromedriver",
                "/home/hoangdao/Storage/MIC-CRAWLED"
        );
        crawler.crawAllByType("Thông tư");
    }

}
