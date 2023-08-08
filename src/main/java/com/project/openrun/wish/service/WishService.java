package com.project.openrun.wish.service;

import com.project.openrun.member.entity.Member;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import com.project.openrun.wish.dto.WishResponseDto;
import com.project.openrun.wish.entity.Wish;
import com.project.openrun.wish.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.project.openrun.global.exception.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishResponseDto createWish(Long productId, Member member) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("상품")
        ));

        if (wishRepository.findByProductAndMember(product, member).isPresent()) {
            throw new ResponseStatusException(DUPLICATE_DATA.getStatus(), DUPLICATE_DATA.formatMessage("관심 상품"));
        }
        wishRepository.save(
                Wish.builder()
                        .product(product)
                        .member(member)
                        .build()
        );

        product.addWish();

        return new WishResponseDto(product.getWishCount());
    }

    public WishResponseDto deleteWish(Long productId, Member member) {

        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("상품"))
        );

        Wish wish = wishRepository.findByProductAndMember(product, member).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("관심 상품"))
        );

        wishRepository.delete(wish);

        product.deleteWish();

        return new WishResponseDto(product.getWishCount());
    }
}
