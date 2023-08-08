package com.project.openrun.product.service;


import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.dto.ProductSearchCondition;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.openrun.global.exception.type.ErrorCode.NOT_FOUND_DATA;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<AllProductResponseDto> getAllProducts(Pageable pageable) {
        if (productRepository.findAll(pageable).isEmpty()) {
            log.info("[ProductService getAllProducts] emptyList");
//            return Collections.emptyList();
            return null;
        }

        Page<Product> result = productRepository.findAll(pageable);
        return result.map((entity) ->
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
                ));
    }

    public DetailProductResponseDto getDetailProduct(Long productId) {
        Product findProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("해당 상품"))
        );

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

    public Page<AllProductResponseDto> searchAllProducts(ProductSearchCondition condition, Pageable pageable) {
        Page<AllProductResponseDto> allProductResponseDtos = productRepository.searchAllProducts(condition, pageable);

        return allProductResponseDtos;
    }

    public List<AllProductResponseDto> getTopCountProducts(Long count) {
        return productRepository.findTopCountProduct(count).stream()
                .map((product) -> new AllProductResponseDto(
                        product.getId(),
                        product.getProductName(),
                        product.getProductImage(),
                        product.getPrice(),
                        product.getMallName(),
                        product.getCurrentQuantity(),
                        product.getEventStartTime(),
                        product.getCategory(),
                        product.getTotalQuantity(),
                        product.getWishCount()
                ))
                .collect(Collectors.toList());
    }

    public Page<AllProductResponseDto> getOpenrunAllProducts(Pageable pageable) {
        return productRepository.findAllByStatusOrderByWishCountDesc(OpenRunStatus.OPEN, pageable)
                .map((product) -> new AllProductResponseDto(
                        product.getId(),
                        product.getProductName(),
                        product.getProductImage(),
                        product.getPrice(),
                        product.getMallName(),
                        product.getCurrentQuantity(),
                        product.getEventStartTime(),
                        product.getCategory(),
                        product.getTotalQuantity(),
                        product.getWishCount()
                ));
    }
}
