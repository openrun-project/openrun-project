package com.project.openrun.product.api.service;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;



class NaverUriBuilderServiceTest {


    @Test
    public void Test() {
        // given
        String query = "상의";
        int display = 10;
        int start = 1;

        //when
        NaverUriBuilderService naverUriBuilderService = new NaverUriBuilderService();
        URI uri = naverUriBuilderService.buildUriByQueryAndDisplayAndStart(query, display, start);
        String decodedResult = URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);

        //then
        assertThat(decodedResult).isEqualTo("https://openapi.naver.com/v1/search/shop.json?query=상의&display=10&start=1&sort=date");
    }

}