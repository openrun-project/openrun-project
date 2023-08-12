package com.project.openrun.product.service;


import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.AllProductResponseDtos;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.openrun.global.exception.type.ErrorCode.NOT_FOUND_DATA;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<AllProductResponseDto> getAllProducts(Pageable pageable) {
        // 인덱싱 적용 고려중
        Page<AllProductResponseDto> result = productRepository.findAllDto(pageable);

        if (Objects.isNull(result) || !result.hasContent()) {
            log.info("[ProductService getAllProducts] emptyList");
            return null;
        }

        return result;
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
                findProduct.getWishCount(),
                findProduct.getStatus()
        );
    }

    //테스트 코드 작성 필요
    public Page<AllProductResponseDto> searchAllProducts(ProductSearchCondition condition, Pageable pageable) {
        Page<AllProductResponseDto> allProductResponseDtos = productRepository.searchAllProducts(condition, pageable);

        return allProductResponseDtos;
    }

    public List<AllProductResponseDtos> getTopCountProducts(Long count) {
        //querydsl로 projections 필요함
        return productRepository.findTopCountProduct(count).stream()
                .map((product) -> new AllProductResponseDtos(
                        product.getId(),
                        product.getProductName(),
                        product.getProductImage(),
                        product.getPrice(),
                        product.getMallName(),
                        product.getCategory()

                ))
                .collect(Collectors.toList());
    }

    public Page<AllProductResponseDtos> getOpenrunAllProducts(Pageable pageable) {
        //querydsl로 projections 필요함
        return productRepository.findAllByStatusOrderByWishCountDescProductNameDesc(OpenRunStatus.OPEN, pageable)
                .map((product) -> new AllProductResponseDtos(
                        product.getId(),
                        product.getProductName(),
                        product.getProductImage(),
                        product.getPrice(),
                        product.getMallName(),
                        product.getCategory()

                ));
    }
}
