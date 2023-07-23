package com.hoangdao.seleniumcrawler.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.hoangdao.seleniumcrawler.demo.entity.Magazine;
import com.hoangdao.seleniumcrawler.demo.entity.MagazineContent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumCrawlerService {

    private static SeleniumCrawlerService INSTANCE = null;
    private final WebDriver webDriver;
    private static final String TARGET_DOMAIN = "https://www.toaan.gov.vn/";
    private static final String ROOT_PAGE = "https://www.toaan.gov.vn/webcenter/portal/tatc/tin-xet-xu";

    private SeleniumCrawlerService() {
        System.setProperty("webdriver.chrome.driver", "/home/hoangdao/Downloads/chromedriver/chromedriver");
        webDriver = new ChromeDriver();
    }

    public static final SeleniumCrawlerService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SeleniumCrawlerService();
        }
        return INSTANCE;
    }

    public void craw() {
        webDriver.get(ROOT_PAGE);
        List<WebElement> categoryElements = webDriver.findElements(
                By.cssSelector("#collapseOne1 div")
        );
        List<MagazineCrawlRequest> crawlRequests = new ArrayList<>();
        for (WebElement element : categoryElements) {
            WebElement aElement = element.findElement(By.cssSelector("a"));
            String categoryUrl = aElement.getAttribute("href");
            String categoryName = aElement.getText();
            crawlRequests.addAll(fetchPage(categoryUrl, categoryName));
        }
    }

    private List<MagazineCrawlRequest> fetchCategory(String categoryUrl, String categoryName) {
        System.out.println(categoryUrl);
        List<MagazineCrawlRequest> crawlRequests = new ArrayList<>();
        boolean hasNextPage = true;
        String pageUrl = categoryUrl;
        while (hasNextPage) {
            crawlRequests.addAll(fetchPage(pageUrl, categoryName));

            webDriver.get(pageUrl);
            WebElement paginationElement = webDriver.findElement(By.cssSelector("nav ul.pagination"));
            List<WebElement> pageItemElements = paginationElement.findElements(
                    By.cssSelector("li")
            );

            WebElement nextPageElement = null;
            for (int i = pageItemElements.size() - 1; i >= 0; --i) {
                WebElement webElement = pageItemElements.get(i);
                if (webElement.getText().trim().equals("Sau")) {
                    nextPageElement = webElement;
                    break;
                }
            }

            if (!nextPageElement.getAttribute("class").contains("disabled")) {
                pageUrl = pageUrl.substring(0, pageUrl.lastIndexOf("?")) +
                        nextPageElement.findElement(By.cssSelector("a")).getAttribute("href");
            } else {
                hasNextPage = false;
            }
        }

        return crawlRequests;
    }

    private List<MagazineCrawlRequest> fetchPage(String pageUrl, String categoryName) {
        System.out.println(pageUrl);

        webDriver.get(pageUrl);
        List<MagazineCrawlRequest> crawlRequests = new ArrayList<>();
        List<WebElement> elements = webDriver.findElements(By.cssSelector("ul.media-list>div"));
        for (WebElement element : elements) {
            MagazineCrawlRequest crawlRequest = new MagazineCrawlRequest();
            crawlRequest.setCategory(categoryName);

            WebElement aElement = element.findElement(By.cssSelector("li.row a"));
            crawlRequest.setUrl(aElement.getAttribute("href"));

            WebElement thumbnailElement = aElement.findElement(By.cssSelector("img"));
            crawlRequest.setThumbnail(
                    TARGET_DOMAIN + thumbnailElement.getAttribute("src"));

            crawlRequests.add(crawlRequest);
        }
        return crawlRequests;
    }

    public Magazine crawMagazine(MagazineCrawlRequest crawlRequest) {
        webDriver.get(crawlRequest.getUrl());
        Magazine magazine = new Magazine();
        WebElement contentSection = webDriver.findElement(By.cssSelector("section.row section"));

        WebElement titleElement = contentSection.findElement(
                By.cssSelector("h3.text-do")
        );
        magazine.setTitle(titleElement.getText());

        WebElement timeElement = contentSection.findElement(By.cssSelector("span.text-ngayxam-page"));
        magazine.setCreatedAt(
                parseDateTime(timeElement.getText().substring(1,
                        timeElement.getText().lastIndexOf(")")))
        );
        magazine.setUpdatedAt(magazine.getCreatedAt());

        WebElement shortDescElement = contentSection.findElement(
                By.cssSelector(".content #content-print p")
        );
        magazine.setShortDescription(shortDescElement.getText());

        List<MagazineContent.AbstractContent> contents = new ArrayList<>();
        List<WebElement> contentElements = contentSection.findElements(
                By.cssSelector(".content #content-print div p")
        );
        for (int i = 0; i < contentElements.size(); ++i) {
            WebElement element = contentElements.get(i);
            if (isImageParagraphElement(element)) {
                MagazineContent.ImageContent imageContent = new MagazineContent.ImageContent();
                imageContent.setUrl(element
                        .findElement(By.cssSelector("img"))
                        .getAttribute("src"));
                if (i +1 != contentElements.size() &&
                    isImageTitleParagraphElement(contentElements.get(i + 1))) {
                    imageContent.setTitle(contentElements.get(i + 1).getText());
                }
                contents.add(imageContent);
            } else if (isTextParagraphElement(element)) {
                MagazineContent.TextContent textContent = new MagazineContent.TextContent();
                textContent.setRawText(element.getAttribute("innerHTML"));
                contents.add(textContent);
            }
        }
        magazine.setContents(contents);
        return magazine;
    }

    private boolean isImageTitleParagraphElement(WebElement element) {
        return !isImageParagraphElement(element) &&
                element.findElement(By.cssSelector("span"))
                        .getAttribute("style").contains("color: #0070C0;");
    }

    private boolean isTextParagraphElement(WebElement element) {
        return !isImageParagraphElement(element) && !isImageTitleParagraphElement(element);
    }

    private boolean isImageParagraphElement(WebElement element) {
        return element.findElement(By.cssSelector("img")) != null;
    }

    //  Receive format: dd/MM/yyyy HH:mm
    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}