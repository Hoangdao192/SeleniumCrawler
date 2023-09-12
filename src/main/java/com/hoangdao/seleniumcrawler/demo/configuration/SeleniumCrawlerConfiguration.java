package com.hoangdao.seleniumcrawler.demo.configuration;

import com.hoangdao.seleniumcrawler.demo.service.WebDriverConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class SeleniumCrawlerConfiguration {

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProxyConfig {
        private boolean enable = false;
        private String host;
        private String port;
        private String username;
        private String password;
    }

    private WebDriverConfig driver;
    private ProxyConfig proxy;

}
