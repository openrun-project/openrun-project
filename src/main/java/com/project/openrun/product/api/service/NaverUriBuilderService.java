package com.project.openrun.product.api.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class NaverUriBuilderService {

    private static final String NAVER_SHOPPING_URL = "https://openapi.naver.com";
    private static final String NAVER_PATH = "/v1/search/shop.json";
    public URI buildUriByQueryAndDisplayAndStart(String query, int display, int start) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(NAVER_SHOPPING_URL).path(NAVER_PATH);

        uriBuilder.queryParam("query", query)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", "date");

        URI uri = uriBuilder.build().encode().toUri();
        log.info("[NaverUriBuilderService buildUriByQueryAndDisplayAndStart] uri: {}", uri);


        return uri;
    }
}
