package com.hoangdao.seleniumcrawler.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoangdao.seleniumcrawler.demo.configuration.SeleniumCrawlerConfiguration;
import com.hoangdao.seleniumcrawler.demo.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class MICCrawler {

    private WebDriver webDriver;
    private static final String HOST = "https://mic.gov.vn";
    private static final String ROOT_PATH = "https://mic.gov.vn/mic_2020/Pages/VanBan/danhsachvanban.aspx?LVB=100";
    private final File storageDirectory;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public static class CrawledDocument {
        public String code;
        public String issuedAgency;
        public String type;
        public String field;
        public String url;
        public List<String> downloadUrls = new ArrayList<>();
//        public List<File> downloadFiles = new ArrayList<>();
        public File file;
        public String publishDate;
        public String effectiveDate;
        public String expiryDate;
        public String signer;

        public CrawledDocument(
                String code, String issuedAgency,
                String type, String field,
                String url, File file, String publishDate) {
            this.code = code;
            this.issuedAgency = issuedAgency;
            this.type = type;
            this.field = field;
            this.url = url;
            this.file = file;
            this.publishDate = publishDate;
        }
    }

    public MICCrawler(String chromeDriverPath, String storageDirectory) {
        this.storageDirectory = new File(storageDirectory);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        webDriver = new ChromeDriver(options);
        httpClient = new OkHttpClient();
        objectMapper = new ObjectMapper();
    }

    public List<CrawledDocument> crawAllByType(String type) {
        List<CrawledDocument> crawledDocuments = new ArrayList<>();
        webDriver.get(ROOT_PATH);

        //  Auto click filter
        WebElement filterBtn = webDriver.findElement(By.cssSelector("#btnFilter"));
        filterBtn.click();
        WebElement documentTypeSelectBox = webDriver.findElement(
                By.cssSelector("#ctl00_ctl46_g_9442cf22_ef24_4aef_bcc6_e2c7f6ab9156_ctl00_ddlHinhThuc")
        );
        documentTypeSelectBox.click();
        List<WebElement> selectBoxOptions = documentTypeSelectBox.findElements(
                By.cssSelector("option")
        );
        for (WebElement element : selectBoxOptions) {
            if (element.getText().toUpperCase().equals(type.toUpperCase())) {
                element.click();
                break;
            }
        }
        WebElement searchBtn = webDriver.findElement(By.cssSelector(
                "#btn-search"
        ));
        searchBtn.click();

        boolean hasNext = true;
        int page = 0;
        while (hasNext) {
            log.info("Craw page {}", page);
            crawledDocuments.addAll(getDocumentListInPage(currentPageAsJsoup(webDriver)));
            WebElement pagination = webDriver.findElement(
                    By.cssSelector("div#pagevanbant ul.pagination")
            );
            List<WebElement> pages = pagination.findElements(
                    By.cssSelector("li")
            );
            hasNext = pages
                    .stream()
                    .anyMatch(webElement -> webElement.getText().equals("Sau>>"));
            if (hasNext) {
                Optional<WebElement> next = pages
                        .stream()
                        .filter(webElement -> webElement.getText().equals("Sau>>"))
                        .findAny();
                if (next.isPresent()) {
                    next.get().click();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return crawledDocuments;
    }

    public List<CrawledDocument> getDocumentListInPage(Document page) {
        List<CrawledDocument> crawledDocuments = new ArrayList<>();
        Element table = page.selectFirst(".table-vb");
        List<Element> rows = table.select("tbody > tr");
        //  Remove head row
        rows.remove(0);
        for (Element row : rows) {
            List<Element> cells = row.select("td");
            Element code = cells.get(0);
            Element issuedAgency = cells.get(1);
            Element type = cells.get(2);
            Element field = cells.get(3);
            Element fileUrl = cells.get(4).selectFirst("a");
            Element date = cells.get(5);
            CrawledDocument crawledDocument = new CrawledDocument(
                    code.text(), issuedAgency.text(), type.text(),
                    field.text(), HOST + fileUrl.attr("href"),
                    null, date.text()
            );
            try {
                crawledDocuments.add(
                    crawDocument(crawledDocument)
                );
            } catch (IOException e) {
                log.error("Cannot craw " + crawledDocument.url);
            }
        }
        return crawledDocuments;
    }

    private Document getPage(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get().build();
        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return Jsoup.parse(response.body().string());
        }
        return null;
    }

    public CrawledDocument crawDocument(CrawledDocument crawledDocument) throws IOException {
        log.info("Craw document {} {}", crawledDocument.code, crawledDocument.url);
        Document page = getPage(crawledDocument.url);
        Element contentTable = page.selectFirst(".main_vbtable");
        List<Element> rows = contentTable.select("tr");
        Element effectiveDate = rows.get(3).selectFirst("td:nth-child(2)");
        Element expiryDate = rows.get(4).selectFirst("td:nth-child(2)");
        Element signer = rows.get(8).selectFirst("td:nth-child(2)");
        Element download = rows.get(11).selectFirst("td:nth-child(2)");
        List<Element> downloadLinkElement = download.select("a");
        List<String> downloadUrls = downloadLinkElement.stream().map(element ->
            HOST + element.attr("href")).collect(Collectors.toList());
        crawledDocument.effectiveDate = effectiveDate.text();
        crawledDocument.expiryDate = expiryDate.text();
        crawledDocument.signer = signer.text();
        crawledDocument.downloadUrls = downloadUrls;
        File saveDirectory = Path.of(
                storageDirectory.getAbsolutePath(),
                crawledDocument.type,
                crawledDocument.code.replace("/", "-")
        ).toFile();
        saveDirectory.mkdirs();
        try {
            FileHelper.save(
                    objectMapper.writeValueAsBytes(crawledDocument),
                    new File(saveDirectory,
                            crawledDocument.type + " "
                    + crawledDocument.code.replace("/", "-") + ".json")
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String url : downloadUrls) {
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("url", url)
                        .build();
                Request request = new Request.Builder()
                        .url("http://localhost:8080/api/forward/download/file")
                        .post(requestBody).build();
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String fileName = "file" + System.currentTimeMillis();
                    if (url.split("/").length > 0) {
                        fileName = url.split("/")[url.split("/").length - 1];
                    }
                    File saveFile = new File(saveDirectory,
                            fileName);
                    FileHelper.save(response.body().bytes(), saveFile);
//                    crawledDocument.downloadFiles.add(saveFile);
                } else {
                    log.error("Cannot download file {}", url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return crawledDocument;
    }

    private Document currentPageAsJsoup(WebDriver webDriver) {
        WebElement webElement = webDriver.findElement(By.cssSelector("body"));
        return Jsoup.parse(
                webElement.getAttribute("innerHTML")
        );
    }

}
