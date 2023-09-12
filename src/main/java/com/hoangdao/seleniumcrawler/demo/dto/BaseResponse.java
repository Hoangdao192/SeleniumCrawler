package com.hoangdao.seleniumcrawler.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {

    private int code;
    private String message;
    private T data;


    public boolean isSuccessful() {
        return code >= 200 && code <= 300;
    }
}
