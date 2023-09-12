package com.hoangdao.seleniumcrawler.demo.controller;

import com.hoangdao.seleniumcrawler.demo.dto.BaseResponse;
import com.hoangdao.seleniumcrawler.demo.service.ForwardService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/forward")
public class ForwardController {

    private final ForwardService forwardService;

    public ForwardController(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @PostMapping("download/file")
    public ResponseEntity<?> downloadFile(@RequestParam String url) {
        BaseResponse<byte[]> baseResponse = forwardService.downloadFile(url);
        if (baseResponse.isSuccessful()) {
            String fileName = "file";
            if (url.split("/").length > 0) {
                fileName = url.split("/")[url.split("/").length - 1];
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(baseResponse.getData());
        }
        return ResponseEntity.badRequest().build();
    }
}
