package com.project.openrun.product.service;


import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.dto.ProductSearchCondition;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.CacheRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import com.project.openrun.product.repository.ProductsSearchRepository;
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
    private final CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;
    private final CacheRedisRepository<AllProductResponseDto> allProductRedisRepository;
    private final ProductsSearchRepository productsSearchRepository;

    public Page<AllProductResponseDto> getAllProducts(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();

        Page<AllProductResponseDto> productsInRedis = allProductRedisRepository.getProduct(pageNumber);

        if (Objects.isNull(productsInRedis)) {

            Long count = allProductRedisRepository.getProductCount().orElseGet(() -> {
                long countResult = productRepository.count();
                allProductRedisRepository.saveProductCount(countResult);
                return countResult;
            });

            Page<AllProductResponseDto> productsInDB = productRepository.findAllDto(pageable, count);

            allProductRedisRepository.saveProduct(pageNumber, productsInDB);

            return productsInDB;
        }

        return productsInRedis;
    }


    public DetailProductResponseDto getDetailProduct(Long productId) {

        Integer currentQuantity = openRunProductRedisRepository.getCurrentQuantityCount(productId);

        Product findProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("해당 상품"))
        );

        if (currentQuantity == null && OpenRunStatus.OPEN.equals(findProduct.getStatus())) {
            openRunProductRedisRepository.saveCurrentQuantityCount(productId, findProduct.getCurrentQuantity());
            currentQuantity = openRunProductRedisRepository.getCurrentQuantityCount(productId);
        }

        return new DetailProductResponseDto(
                findProduct.getId(),
                findProduct.getProductName(),
                findProduct.getProductImage(),
                findProduct.getPrice(),
                findProduct.getMallName(),
                currentQuantity,
                findProduct.getEventStartTime(),
                findProduct.getCategory(),
                findProduct.getTotalQuantity(),
                findProduct.getWishCount(),
                findProduct.getStatus()
        );
    }


    public Page<AllProductResponseDto> searchAllProducts(ProductSearchCondition condition, Pageable pageable) {
        Page<AllProductResponseDto> allProductResponseDtos = productsSearchRepository.searchAllProductsUsingFullText(condition, pageable);

        return allProductResponseDtos;
    }

    public List<OpenRunProductResponseDto> getTopCountProducts(Long count) {

        return productRepository.findTopCountProduct(count).stream()
                .map((product) -> new OpenRunProductResponseDto(
                        product.getId(),
                        product.getProductName(),
                        product.getProductImage(),
                        product.getPrice(),
                        product.getMallName(),
                        product.getCategory()

                ))
                .collect(Collectors.toList());
    }

    public Page<OpenRunProductResponseDto> getOpenRunAllProducts(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        Page<OpenRunProductResponseDto> productsInRedis = openRunProductRedisRepository.getProduct(pageNumber);

        if (Objects.isNull(productsInRedis)) {
            Long count = openRunProductRedisRepository.getProductCount().orElseGet(() -> {
                Long countResult = productRepository.countByStatus(OpenRunStatus.OPEN);
                openRunProductRedisRepository.saveProductCount(countResult);
                return countResult;
            });
            Page<OpenRunProductResponseDto> productsInDB = productRepository.findOpenRunProducts(OpenRunStatus.OPEN, pageable, count);
            openRunProductRedisRepository.saveProduct(pageNumber, productsInDB);
            return productsInDB;
        }

        return productsInRedis;
    }

}
