package com.hoangdao.seleniumcrawler.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Document
public class Magazine {
    private String id;
    private String title;
    private String shortDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MagazineContent.AbstractContent> contents;
    private String url;
    private String thumbnail;
}
