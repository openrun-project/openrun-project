package com.project.openrun.product.service;


import com.project.openrun.global.exception.ProductException;
import com.project.openrun.global.exception.type.ProductErrorCode;
import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<AllProductResponseDto> getAllProducts() {
        if (productRepository.findAll().isEmpty()) {
            log.info("[ProductService getAllProducts] emptyList");
            return Collections.emptyList();
        }

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
                .orElseThrow(() -> new ProductException(ProductErrorCode.NO_PRODUCT_SEARCH));

        return new DetailProductResponseDto(
                findProduct.getId(),
                findProduct.getProductName(),
                findProduct.getProductImage(),
                findProduct.getPrice(),
                findProduct.getMallName(),
                findProduct.getCurrentQuantity(),
                findProduct.getEventStartTime(),
                findProduct.getCategory(),
                findProduct.getTotalQuantity(),
                findProduct.getWishCount()
        );
    }
}
