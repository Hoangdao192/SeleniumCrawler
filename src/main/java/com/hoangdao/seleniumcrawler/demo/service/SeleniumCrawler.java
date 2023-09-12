package com.hoangdao.seleniumcrawler.demo.service;

import com.hoangdao.seleniumcrawler.demo.configuration.SeleniumCrawlerConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class SeleniumCrawler {

    protected SeleniumCrawlerConfiguration crawlerConfiguration;
    protected WebDriver webDriver;

    @Autowired
    public SeleniumCrawler(SeleniumCrawlerConfiguration crawlerConfiguration) {
        this.crawlerConfiguration = crawlerConfiguration;
        validateConfiguration();
    }

    private void validateConfiguration() {
        System.out.println(crawlerConfiguration);
        if (crawlerConfiguration.getDriver() != null) {
            WebDriverConfig webDriverConfig = crawlerConfiguration.getDriver();
            if (webDriverConfig.getPath() == null ||
                !webDriverConfig.getPath().exists() || !webDriverConfig.getPath().isFile()) {
                throw new RuntimeException("Incorrect webdriver configuration");
            }
            if (webDriverConfig.getType() == null) {
                throw new RuntimeException("Incorrect webdriver configuration");
            }
        } else {
            throw new RuntimeException("Webdriver is not configured yet.");
        }

        if (crawlerConfiguration.getProxy() != null) {
            SeleniumCrawlerConfiguration.ProxyConfig proxyConfig = crawlerConfiguration.getProxy();
            if (proxyConfig.isEnable()) {
                if (proxyConfig.getHost() == null) {
                    throw new RuntimeException("Selenium proxy is enable but missing proxy host.");
                }
                if (proxyConfig.getPort() == null) {
                    throw new RuntimeException("Selenium proxy is enable but missing proxy port.");
                }
                if (proxyConfig.getUsername() != null && proxyConfig.getPassword() == null) {
                    throw new RuntimeException("Selenium proxy authentication is missing password.");
                }
            }
        }
    }

    public Document getWebPage(String url) {
        webDriver.get(url);
        byPassPrivacyError(webDriver);
        return Jsoup.parse(webDriver.findElement(By.cssSelector("html")).getAttribute("innerHTML"));
    }

    private void byPassPrivacyError(WebDriver webDriver) {
//        webDriver.findElement("")
    }

}
