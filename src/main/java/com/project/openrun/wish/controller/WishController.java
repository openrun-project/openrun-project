package com.project.openrun.wish.controller;

import com.project.openrun.auth.security.UserDetailsImpl;
import com.project.openrun.wish.dto.WishResponseDto;
import com.project.openrun.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class WishController {

    private final WishService wishService;

    @PostMapping("/{productId}/wish")
    public WishResponseDto createWish (@PathVariable Long productId , @AuthenticationPrincipal UserDetailsImpl userDetails){
        return wishService.createWish(productId, userDetails.getMember());

    }
    @DeleteMapping("/{productId}/wish")
    public WishResponseDto deleteWish (@PathVariable Long productId , @AuthenticationPrincipal UserDetailsImpl userDetails){
        return wishService.deleteWish(productId, userDetails.getMember());
    }



}
