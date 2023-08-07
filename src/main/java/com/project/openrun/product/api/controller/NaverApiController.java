package com.project.openrun.product.api.controller;


import com.project.openrun.product.api.dto.CreateDataRequestDto;
import com.project.openrun.product.api.service.NaverApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NaverApiController {

    private final NaverApiService naverApiService;

    @PostMapping("/products")
    public void createData(@RequestBody CreateDataRequestDto requestDto) {
        naverApiService.createItemForNaverApi(requestDto);
    }
}
