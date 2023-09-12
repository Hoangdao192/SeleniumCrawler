package com.hoangdao.seleniumcrawler.demo.service;

import com.hoangdao.seleniumcrawler.demo.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ForwardService {

    private final OkHttpClient httpClient;

    public ForwardService() {
        httpClient = new OkHttpClient();
    }

    public BaseResponse<byte[]> downloadFile(String url) {
        BaseResponse<byte[]> baseResponse = new BaseResponse<>();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get().build();
            Response response = httpClient.newCall(request)
                    .execute();
            if (response.isSuccessful()) {
                baseResponse.setCode(200);
                ResponseBody responseBody = response.body();
                baseResponse.setData(responseBody.bytes());
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
        }
        return baseResponse;
    }

}
