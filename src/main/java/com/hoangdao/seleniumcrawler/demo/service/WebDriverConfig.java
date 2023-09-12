package com.hoangdao.seleniumcrawler.demo.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class WebDriverConfig {

    public enum Type {
        CHROME, FIREFOX
    }

    private File path;
    private Type type;

}
