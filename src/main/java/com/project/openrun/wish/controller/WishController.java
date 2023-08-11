package com.project.openrun.wish.controller;

import com.project.openrun.auth.security.UserDetailsImpl;
import com.project.openrun.wish.dto.IsWishResponseDto;
import com.project.openrun.wish.dto.WishProductResponseDto;
import com.project.openrun.wish.dto.WishResponseDto;
import com.project.openrun.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class WishController {

    private final WishService wishService;

    @PostMapping("/{productId}/wish")
    public WishResponseDto createWish(@PathVariable Long productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishService.createWish(productId, userDetails.getMember());

    }

    @DeleteMapping("/{productId}/wish")
    public WishResponseDto deleteWish(@PathVariable Long productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishService.deleteWish(productId, userDetails.getMember());
    }

    @GetMapping("/wish")
    public Page<WishProductResponseDto> getMyWishProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable
    ) {
        return wishService.getMyWishProduct(userDetails.getMember(), pageable);
    }

    @GetMapping("/{productId}/wish/user")
    public IsWishResponseDto getProductWishUser(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return wishService.getProductWishUser(productId, userDetails.getMember());
    }
}
