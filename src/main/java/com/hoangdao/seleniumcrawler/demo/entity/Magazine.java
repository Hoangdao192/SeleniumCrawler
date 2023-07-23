package com.hoangdao.seleniumcrawler.demo.entity;

import java.time.LocalDateTime;
import java.util.List;


public class Magazine {

    private String id;
    private String title;
    private String shortDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MagazineContent.AbstractContent> contents;
    private String url;
    private String thumbnail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<MagazineContent.AbstractContent> getContents() {
        return contents;
    }

    public void setContents(List<MagazineContent.AbstractContent> contents) {
        this.contents = contents;
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
