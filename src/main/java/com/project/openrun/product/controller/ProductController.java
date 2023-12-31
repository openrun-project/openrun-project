package com.project.openrun.product.controller;


import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.dto.ProductSearchCondition;
import com.project.openrun.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<AllProductResponseDto> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/{productId}")
    public DetailProductResponseDto getDetailProduct(@PathVariable Long productId) {
        return productService.getDetailProduct(productId);
    }

    @GetMapping("/search")
    public Page<AllProductResponseDto> searchAllProducts(ProductSearchCondition condition, Pageable pageable) {
        return productService.searchAllProducts(condition, pageable);
    }

    @GetMapping("/wishcount/{count}")
    public List<OpenRunProductResponseDto> getTopCountProducts(@PathVariable("count") Long count) {
        return productService.getTopCountProducts(count);
    }

    @GetMapping("/openrun")
    public Page<OpenRunProductResponseDto> getOpenrunAllProducts(Pageable pageable) {
        return productService.getOpenRunAllProducts(pageable);
    }

}
