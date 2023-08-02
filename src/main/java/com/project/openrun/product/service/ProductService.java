package com.project.openrun.product.service;


import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<AllProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map((entity) ->
                        AllProductResponseDto.builder()
                                .id(entity.getId())
                                .productImage(entity.getProductImage())
                                .productName(entity.getProductName())
                                .price(entity.getPrice())
                                .currentQuantity(entity.getCurrentQuantity())
                                .eventStartTime(entity.getEventStartTime())
                                .mallName(entity.getMallName())
                                .totalQuantity(entity.getTotalQuantity())
                                .category(entity.getCategory())
                                .wishCount(entity.getWishCount())
                                .build()
                ).collect(Collectors.toList());
    }

    public DetailProductResponseDto getDetailProduct(Long productId) {
        Product findProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        return DetailProductResponseDto.builder()
                .id(findProduct.getId())
                .productImage(findProduct.getProductImage())
                .productName(findProduct.getProductName())
                .price(findProduct.getPrice())
                .currentQuantity(findProduct.getCurrentQuantity())
                .eventStartTime(findProduct.getEventStartTime())
                .mallName(findProduct.getMallName())
                .totalQuantity(findProduct.getTotalQuantity())
                .category(findProduct.getCategory())
                .wishCount(findProduct.getWishCount())
                .build();
    }
}
