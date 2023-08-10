package com.project.openrun.global.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/openrun")
public class PageController {

    @GetMapping("/page/or")
    public String goOpenrunPage() {
        return "openrun";
    }

    @GetMapping("/mypage")
    public String goMyPage() {
        return "mypage";
    }

    @GetMapping("/detail/{productId}")
    public String goProductDetailPage(@PathVariable("productId") Long id, Model model) {
        model.addAttribute("productId", id);
        return "detail";
    }

    @GetMapping("/total")
    public String goAllProductPage() {
        return "total";
    }

    @GetMapping("/main")
    public String goMainPage() {
        System.out.println("메인 페이지 컨트롤러");
        return "main";
    }

}