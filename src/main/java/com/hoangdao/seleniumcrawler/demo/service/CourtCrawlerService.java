package com.hoangdao.seleniumcrawler.demo.service;

import com.hoangdao.seleniumcrawler.demo.configuration.SeleniumCrawlerConfiguration;
import com.hoangdao.seleniumcrawler.demo.entity.Magazine;
import com.hoangdao.seleniumcrawler.demo.entity.MagazineContent;
import com.hoangdao.seleniumcrawler.demo.repository.MagazineRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourtCrawlerService extends SeleniumCrawler {

    private static final String TARGET_DOMAIN = "https://www.toaan.gov.vn/";
    private static final String ROOT_PAGE = "https://www.toaan.gov.vn/webcenter/portal/tatc/tin-xet-xu";
    @Autowired
    private MagazineRepository magazineRepository;

    public CourtCrawlerService(SeleniumCrawlerConfiguration crawlerConfiguration) {
        super(crawlerConfiguration);
    }

    public void craw() {
        Document document = getWebPage(ROOT_PAGE);

        Elements categoryElements = document.select("#collapseOne1 div");
        List<MagazineCrawlRequest> crawlRequests = new ArrayList<>();
        for (Element element : categoryElements) {
            try {
                Element aElement = element.selectFirst("a");
                String categoryUrl = aElement.attr("href");
                String categoryName = aElement.text();
                crawlRequests.addAll(fetchPage(categoryUrl, categoryName));
            } catch (NullPointerException e) {
                System.out.println("Null a element");
            }
        }

        for (MagazineCrawlRequest crawlRequest : crawlRequests) {
            Magazine magazine = crawMagazine(crawlRequest);
            magazineRepository.save(magazine);
        }
    }

    private List<MagazineCrawlRequest> fetchCategory(String categoryUrl, String categoryName) {
        System.out.println(categoryUrl);
        List<MagazineCrawlRequest> crawlRequests = new ArrayList<>();
        boolean hasNextPage = true;
        String pageUrl = categoryUrl;
        while (hasNextPage) {
            crawlRequests.addAll(fetchPage(pageUrl, categoryName));

            try {
                Document document = getWebPage(pageUrl);
                Element paginationElement = document.selectFirst("nav ul.pagination");
                Elements pageItemElements = paginationElement.select("li");

                Element nextPageElement = null;
                for (int i = pageItemElements.size() - 1; i >= 0; --i) {
                    Element webElement = pageItemElements.get(i);
                    if (webElement.text().trim().equals("Sau")) {
                        nextPageElement = webElement;
                        break;
                    }
                }

                if (!nextPageElement.hasClass("disabled")) {
                    pageUrl = pageUrl.substring(0, pageUrl.lastIndexOf("?")) +
                            nextPageElement.selectFirst("a").attr("href");
                } else {
                    hasNextPage = false;
                }
            } catch (NullPointerException e) {
                System.out.println("Not found pagination.");
                hasNextPage = false;
            }
        }

        return crawlRequests;
    }

    private List<MagazineCrawlRequest> fetchPage(String pageUrl, String categoryName) {
        System.out.println(pageUrl);

        Document document = getWebPage(pageUrl);

        List<MagazineCrawlRequest> crawlRequests = new ArrayList<>();
        Elements elements = document.select("ul.media-list>div");
        for (Element element : elements) {
            MagazineCrawlRequest crawlRequest = new MagazineCrawlRequest();
            crawlRequest.setCategory(categoryName);

            Element aElement = element.selectFirst("li.row a");
            crawlRequest.setUrl(aElement.attr("href"));

            Element thumbnailElement = aElement.selectFirst("img");
            crawlRequest.setThumbnail(
                    TARGET_DOMAIN + thumbnailElement.attr("src"));

            crawlRequests.add(crawlRequest);
        }
        return crawlRequests;
    }

    public Magazine crawMagazine(MagazineCrawlRequest crawlRequest) {
        Document document = getWebPage(crawlRequest.getUrl());

        Magazine magazine = new Magazine();
        Element contentSection = document.selectFirst("section.row section");

        Element titleElement = contentSection.selectFirst("h3.text-do");
        magazine.setTitle(titleElement.text());

        Element timeElement = contentSection.selectFirst("span.text-ngayxam-page");
        magazine.setCreatedAt(
                parseDateTime(timeElement.text().substring(1,
                        timeElement.text().lastIndexOf(")")))
        );
        magazine.setUpdatedAt(magazine.getCreatedAt());

        Element shortDescElement = contentSection.selectFirst(".content #content-print p");
        magazine.setShortDescription(shortDescElement.text());

        List<MagazineContent.AbstractContent> contents = new ArrayList<>();
        Elements contentElements = contentSection.select(".content #content-print div p");

        for (int i = 0; i < contentElements.size(); ++i) {
            Element element = contentElements.get(i);
            if (isImageParagraphElement(element)) {
                MagazineContent.ImageContent imageContent = new MagazineContent.ImageContent();
                imageContent.setUrl(element.selectFirst("img").attr("src"));
                if (i +1 != contentElements.size() &&
                    isImageTitleParagraphElement(contentElements.get(i + 1))) {
                    imageContent.setTitle(contentElements.get(i + 1).text());
                }
                contents.add(imageContent);
            } else if (isTextParagraphElement(element)) {
                MagazineContent.TextContent textContent = new MagazineContent.TextContent();
                textContent.setRawText(element.attr("innerHTML"));
                contents.add(textContent);
            }
        }
        magazine.setContents(contents);
        return magazine;
    }

    private boolean isImageTitleParagraphElement(Element element) {
        return !isImageParagraphElement(element) &&
                element.selectFirst("span")
                        .attr("style")
                        .contains("color: #0070C0;");
    }

    private boolean isTextParagraphElement(Element element) {
        return !isImageParagraphElement(element) && !isImageTitleParagraphElement(element);
    }

    private boolean isImageParagraphElement(Element element) {
        return element.selectFirst("img") != null;
    }

    //  Receive format: dd/MM/yyyy HH:mm
    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}