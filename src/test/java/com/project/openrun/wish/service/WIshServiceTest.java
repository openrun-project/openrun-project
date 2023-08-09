package com.project.openrun.wish.service;

import com.project.openrun.member.entity.Member;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import com.project.openrun.wish.dto.WishResponseDto;
import com.project.openrun.wish.entity.Wish;
import com.project.openrun.wish.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WIshServiceTest {

    @Mock
    private WishRepository wishRepository;

    @Mock
    private ProductRepository productRepository;

    private WishService wishService;

    @BeforeEach
    public void setup(){
        this.wishService = new WishService(wishRepository,productRepository);
    }


    @Test
    @DisplayName("찜이 잘 저장되는지, 상품의 찜 개수가 + 되는지 테스트 ")
    public void isWishClick() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .wishCount(54) // 초기 찜 수는 0
                .build();

        //productRepository.findById() 메서드가 호출될 때,
        // 어떤 값이든 인자로 받아 Optional.of(product)를 반환하도록 설정합니다.
        // 상품을 찾아줌
        when(productRepository.findWithLockById(any())).thenReturn(Optional.of(product));

        //wishRepository.findByProductAndMember() 메서드가 호출될 때,
        // 어떤 값이든 인자로 받아 Optional.empty()를 반환하도록 설정합니다.
        // => 즉 찜이 안눌린 상태로 세팅해줌
        when(wishRepository.findByProductAndMember(any(), any())).thenReturn(Optional.empty());

        when(wishRepository.save(any())).thenReturn(any());

        // when
        WishResponseDto result = wishService.createWish(1L, member);

        // then
        // 찜 추가 후 찜 수가 1 증가했는지 확인
        assertEquals(product.getWishCount(), 55);
        // 찜 저장이 호출되었는지 확인
        verify(wishRepository, times(1)).save(any());
        // 응답의 찜 수가 1인지 확인
        assertEquals(result.wishCount(), 55);
    }


    @Test
    @DisplayName("찜이 존재할때 찜이 안되는지 테스트")
    public void testCreateWishWithExistingWish() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .wishCount(0)
                .build();

        Wish wish = Wish.builder()
                .id(1L)
                .member(member)
                .product(product)
                .build();

        when(productRepository.findWithLockById(any())).thenReturn(Optional.of(product));
        when(wishRepository.findByProductAndMember(any(), any())).thenReturn(Optional.of(wish));

        // when & then
        // 이미 찜이 존재하면 WishException이 발생하는지 확인
        assertThrows(ResponseStatusException.class, () -> wishService.createWish(1L, member));
    }

    @Test
    @DisplayName("상품이 존재하지 않을 때 예외 발생 테스트 ")
    public void testCreateWishWithNonExistingProduct() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        when(productRepository.findWithLockById(any())).thenReturn(Optional.empty());

        // when & then
        // 존재하지 않는 상품에 대해 WishException이 발생하는지 확인
        assertThrows(ResponseStatusException.class, () -> wishService.createWish(1L, member));
    }


    @Test
    @DisplayName("찜이 취소가 잘 되는지, 상품의 찜 개수가 - 되는지 테스트 ")
    public void testDeleteWish() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .wishCount(100) // 초기 찜 수는 1
                .build();

        Wish wish = Wish.builder()
                .id(1L)
                .member(member)
                .product(product)
                .build();

        //productRepository.findById() 메서드가 호출될 때,
        // 어떤 값이든 인자로 받아 Optional.of(product)를 반환하도록 설정합니다.
        // 상품을 찾아줌
        when(productRepository.findWithLockById(any())).thenReturn(Optional.of(product));

        //wishRepository.findByProductAndMember() 메서드가 호출될 때,
        // 어떤 값이든 인자로 받아 Optional.of(wish)를 반환하도록 설정합니다.
        // => 즉 찜이 눌린 상태로 세팅해줌
        when(wishRepository.findByProductAndMember(any(), any())).thenReturn(Optional.of(wish));

        // when
        WishResponseDto result = wishService.deleteWish(1L, member);

        // then
        // 찜 삭제 후 찜 수가 1 감소했는지 확인
        assertEquals(product.getWishCount(), 99);
        // 찜 삭제가 호출되었는지 확인
        verify(wishRepository, times(1)).delete(any());
        // 응답의 찜 수가 0인지 확인
        assertEquals(result.wishCount(), 99);
    }

    @Test
    @DisplayName("찜이 되어있지 않다면 예외처리가 잘되는지 테스트")
    public void testDeleteWishWithNonExistingWish() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .wishCount(0)
                .build();

        when(productRepository.findWithLockById(any())).thenReturn(Optional.of(product));
        when(wishRepository.findByProductAndMember(any(), any())).thenReturn(Optional.empty());

        // when & then
        // 찜이 존재하지 않으면 WishException이 발생하는지 확인
        assertThrows(ResponseStatusException.class, () -> wishService.deleteWish(1L, member));
    }

    @Test
    @DisplayName("상품이 존재하지 않을 때 예외 발생 테스트 ")
    public void testDeleteWishWithNonExistingProduct() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();
        //when
        when(productRepository.findWithLockById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(ResponseStatusException.class, () -> wishService.deleteWish(1L, member));
    }

}