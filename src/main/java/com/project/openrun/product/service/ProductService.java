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
                        new AllProductResponseDto(
                                entity.getId(),
                                entity.getProductName(),
                                entity.getProductImage(),
                                entity.getPrice(),
                                entity.getMallName(),
                                entity.getCurrentQuantity(),
                                entity.getEventStartTime(),
                                entity.getCategory(),
                                entity.getTotalQuantity(),
                                entity.getWishCount()
                        ))
                .collect(Collectors.toList());
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
