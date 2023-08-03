package com.project.openrun.orders.service;

import com.project.openrun.global.exception.OrderException;
import com.project.openrun.member.entity.Member;
import com.project.openrun.member.entity.MemberRoleEnum;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.product.entity.Product;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, productRepository);
    }

    @Test
    public void getOrdersTest() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .currentQuantity(100)
                .build();

        Order order = Order.builder()
                .id(1L)
                .product(product)
                .member(member)
                .count(1)
                .totalPrice(product.getPrice() * 1)
                .build();

        Order order2 = Order.builder()
                .id(2L)
                .product(product)
                .member(member)
                .count(3)
                .totalPrice(product.getPrice() * 3)
                .build();

        // when
        when(orderRepository.findAllByMember(any(Member.class)))
                .thenReturn(Optional.of(Arrays.asList(order, order2)));

        List<OrderResponseDto> result = orderService.getOrders(member);

        // then
        // 주문 내역이 있을 경우 주문 내역이 반환되는지 확인
        assertEquals(2, result.size());
        assertEquals(order.getProduct().getProductName(), result.get(0).productName());
        assertEquals(order.getProduct().getPrice(), result.get(0).price());
        assertEquals(order.getProduct().getMallName(), result.get(0).mallName());
        assertEquals(order.getCount(), result.get(0).count());

        assertEquals(order2.getProduct().getProductName(), result.get(1).productName());
        assertEquals(order2.getProduct().getPrice(), result.get(1).price());
        assertEquals(order2.getProduct().getMallName(), result.get(1).mallName());
        assertEquals(order2.getCount(), result.get(1).count());
    }

    @Test
    public void getOrders_NoOrders_ExceptionThrown() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        // when
        when(orderRepository.findAllByMember(any(Member.class))).thenReturn(Optional.empty());

        // then
        OrderException orderException = assertThrows(OrderException.class,
                () -> orderService.getOrders(member));

        assertEquals("주문 내역이 존재하지 않습니다.", orderException.getErrorMsg());
    }

    @Test
    public void postOrdersTest() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        OrderRequestDto orderRequestDto = new OrderRequestDto(2);

        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .currentQuantity(100)
                .totalQuantity(100)
                .build();

        // when
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        orderService.postOrders(product.getId(), orderRequestDto, member);

        verify(orderRepository, times(1)).save(any(Order.class));

        // then
        // 주문 수량만큼 재고 감소
        assertEquals(98, product.getCurrentQuantity());
    }

    @Test
    public void postOrders_ProductNotFound_ExceptionThrown() {
        // given
        OrderRequestDto orderRequestDto = new OrderRequestDto(2);

        Product product = Product.builder()
                .id(1L)
                .build();

        Member member = Member.builder()
                .id(1L)
                .build();

        // when
        // 상품을 찾을 수 없을 때 예외 발생 empty()로 설정
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        OrderException OrderException = assertThrows(
                OrderException.class,
                () -> orderService.postOrders(product.getId(), orderRequestDto, member)
        );

        // then
        assertEquals("해당 상품이 존재하지 않습니다.", OrderException.getErrorMsg());
    }

    @Test
    public void deleteOrdersTest() {
        // given
        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .currentQuantity(98)
                .build();

        Member member = Member.builder()
                .id(1L)
                .build();

        Order order = Order.builder()
                .id(1L)
                .member(member)
                .product(product)
                .count(2)
                .build();

        // when
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));

        orderService.deleteOrders(order.getId(), member);

        verify(orderRepository, times(1)).delete(any(Order.class));

        // then
        // 주문 수량만큼 재고 증가
        assertEquals(100, order.getProduct().getCurrentQuantity());
    }

    @Test
    public void deleteOrders_NoPermission_ExceptionThrown() {
        // given
        Member member1 = Member.builder()
                .id(1L)
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .build();

        Order order = Order.builder()
                .id(1L)
                .member(member1)
                .product(product)
                .build();
        // when
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));

        OrderException orderException = assertThrows(OrderException.class, () -> orderService.deleteOrders(order.getId(), member2));

        // then
        assertEquals("주문을 취소할 권한이 없습니다.", orderException.getErrorMsg());
    }

    @Test
    public void deleteOrders_OrderNotFound_ExceptionThrown() {
        // given
        Long orderId = 1L;

        Member member = Member.builder()
                .id(1L)
                .build();

        // when
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        OrderException orderException = assertThrows(OrderException.class, () -> orderService.deleteOrders(orderId, member));

        // then
        assertEquals("주문 내역이 존재하지 않습니다.", orderException.getErrorMsg());
    }
}