package com.hoangdao.seleniumcrawler.demo.service;

public class MagazineCrawlRequest {
    private String category;
    private String subCategory;
    private String url;
    private String thumbnail;

    public MagazineCrawlRequest(String category, String subCategory, String url, String thumbnail) {
        this.category = category;
        this.subCategory = subCategory;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public MagazineCrawlRequest() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    
    
}
