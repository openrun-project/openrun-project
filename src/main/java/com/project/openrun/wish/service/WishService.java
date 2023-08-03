package com.project.openrun.wish.service;

import com.project.openrun.global.exception.WishException;
import com.project.openrun.global.exception.type.WishErrorCode;
import com.project.openrun.member.entity.Member;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import com.project.openrun.wish.dto.WishResponseDto;
import com.project.openrun.wish.entity.Wish;
import com.project.openrun.wish.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishResponseDto createWish(Long productId, Member member) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new WishException(WishErrorCode.NOT_EXIST_PRODUCT)
        );

        if (wishRepository.findByProductAndMember(product, member).isPresent()) {
            throw new WishException(WishErrorCode.ALREADY_CHOOSE_WISH);
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
                 new WishException(WishErrorCode.NOT_EXIST_PRODUCT)
        );

        Wish wish = wishRepository.findByProductAndMember(product, member).orElseThrow(() ->
                 new WishException(WishErrorCode.NO_CHOOSE_WISH)
        );

        wishRepository.delete(wish);

        product.deleteWish();

        return new WishResponseDto(product.getWishCount());
    }
}
