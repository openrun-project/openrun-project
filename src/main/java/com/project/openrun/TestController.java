package com.project.openrun;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @RequestMapping("/test")
    public String test() {
        log.info("TestController test log 확인");
        return "hello, CICD";
    }
}
