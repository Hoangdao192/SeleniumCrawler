package com.hoangdao.seleniumcrawler.demo.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@AllArgsConstructor
public class PropertyConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "selenium")
    public SeleniumCrawlerConfiguration seleniumCrawlerConfiguration() {
        return new SeleniumCrawlerConfiguration();
    }

}
