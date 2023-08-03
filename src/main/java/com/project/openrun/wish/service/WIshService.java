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

@Service
@RequiredArgsConstructor
@Transactional
public class WIshService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishResponseDto createWish(Long productId, Member member) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new IllegalArgumentException("상품이 없습니다.")
        );

        if (wishRepository.findByProductAndMember(product, member).isPresent()) {
            throw new IllegalArgumentException("이미 찜했습니다.");
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
                new IllegalArgumentException("상품이 없습니다.")
        );

        Wish wish = wishRepository.findByProductAndMember(product, member).orElseThrow(() ->
                new IllegalArgumentException("찜한 상품이 아닙니다.")
        );

        wishRepository.delete(wish);

        product.deleteWish();

        return new WishResponseDto(product.getWishCount());
    }
}
