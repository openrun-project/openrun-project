package com.project.openrun.global.view;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/openrun/main";
    }

    @GetMapping("/health-check")
    @ResponseStatus(HttpStatus.OK)
    public void check() {
    }
}
