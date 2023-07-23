package com.hoangdao.seleniumcrawler.demo.entity;

import java.util.ArrayList;
import java.util.List;

public class MagazineContent {

    public static final String TEXT_CONTENT = "text";
    public static final String IMAGE_CONTENT = "image";

    private List<AbstractContent> contents = new ArrayList<>();


    public static abstract class AbstractContent {
        private String type;
        private int index = 0;

        public AbstractContent() {
        }

        public AbstractContent(String type, int index) {
            this.type = type;
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


    public static class TextContent extends AbstractContent {
        private String asHtml;
        private String rawText;

        public TextContent(int index, String asHtml, String rawText) {
            super(TEXT_CONTENT, index);
            this.asHtml = asHtml;
            this.rawText = rawText;
        }

        public TextContent() {
            super(TEXT_CONTENT, 0);
        }

        public String getAsHtml() {
            return asHtml;
        }

        public void setAsHtml(String asHtml) {
            this.asHtml = asHtml;
        }

        public String getRawText() {
            return rawText;
        }

        public void setRawText(String rawText) {
            this.rawText = rawText;
        }
    }


    public static class ImageContent extends AbstractContent {
        private String url;
        private String title;

        public ImageContent(int index, String url, String title) {
            super(IMAGE_CONTENT, index);
            this.url = url;
            this.title = title;
        }

        public ImageContent() {
            super(IMAGE_CONTENT, 0);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
