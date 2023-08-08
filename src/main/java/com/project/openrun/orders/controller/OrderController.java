package com.project.openrun.orders.controller;


import com.project.openrun.auth.security.UserDetailsImpl;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponseDto> getOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return orderService.getOrders(userDetails.getMember());
    }

    @PostMapping("/{productId}")
    public String postOrders(
            @PathVariable Long productId,
            @RequestBody OrderRequestDto count,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        orderService.postOrders(productId, count, userDetails.getMember());

        return "구매 성공";
    }

    @DeleteMapping("/{orderId}")
    public String deleteOrders(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        orderService.deleteOrders(orderId, userDetails.getMember());
        return "구매 취소 성공";
    }
}

