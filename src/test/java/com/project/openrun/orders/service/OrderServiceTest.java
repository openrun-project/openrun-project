package com.project.openrun.orders.service;

import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.global.kafka.producer.OrderCreateProducer;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.entity.OrderStatus;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.OpenRunProductRedisRepositoryImpl;
import com.project.openrun.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderCreateProducer orderCreateProducer;

    @Mock
    private OpenRunProductRedisRepositoryImpl openRunProductRedisRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    public void testGetOrdersTest() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .productName("상품" + 1)
                .currentQuantity(100)
                .build();

        OrderResponseDto order1 = new OrderResponseDto(
                1L,
                product.getProductName(),
                product.getPrice(),
                product.getMallName(),
                1,
                LocalDateTime.now(),
                OrderStatus.SUCCESS
        );

        OrderResponseDto order2 = new OrderResponseDto(
                2L,
                product.getProductName(),
                product.getPrice(),
                product.getMallName(),
                1,
                LocalDateTime.now(),
                OrderStatus.SUCCESS
        );

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "modifiedAt");

        Pageable pageable = PageRequest.of(0, 10, sort);

        // when
        when(orderRepository.findAllByMember(member, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(order1, order2), pageable, 2));

        Page<OrderResponseDto> result = orderService.getOrders(member, pageable);

        // then
        // 주문 내역이 있을 경우 주문 내역이 반환되는지 확인
        assertEquals(2, result.getContent().size());
        assertEquals(order1.productName(), result.getContent().get(0).productName());
        assertEquals(order1.price(), result.getContent().get(0).price());
        assertEquals(order1.mallName(), result.getContent().get(0).mallName());
        assertEquals(order1.count(), result.getContent().get(0).count());

    }

    @Test
    public void testGetOrders_NoOrders_ExceptionThrown() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();

        // when
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "modifiedAt");

        Pageable pageable = PageRequest.of(0, 10, sort);
        when(orderRepository.findAllByMember(any(Member.class), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        // then
        ResponseStatusException orderException = assertThrows(ResponseStatusException.class,
                () -> orderService.getOrders(member, pageable));

        assertEquals("400 BAD_REQUEST \"데이터가 존재하지 않습니다. 사유 : 주문\"", orderException.getMessage());
    }

    @Test
    public void testPostOrdersSuccess() {
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

        when(openRunProductRedisRepository.decreaseQuantity(any(), any())).thenReturn(0L);

        orderService.postOrders(product.getId(), orderRequestDto, member);

        verify(openRunProductRedisRepository, times(1)).decreaseQuantity(product.getId(), orderRequestDto.count());

        verify(orderCreateProducer, times(1)).createOrder(any(OrderEventDto.class));

    }

    @Test
    public void testPostOrdersFail() {
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

        when(openRunProductRedisRepository.decreaseQuantity(any(), any())).thenReturn(-1L);

        assertThrows(ResponseStatusException.class, () -> orderService.postOrders(product.getId(), orderRequestDto, member), "데이터가 존재하지 않습니다. 사유 : 재고 부족");

        verify(openRunProductRedisRepository, times(1)).decreaseQuantity(product.getId(), orderRequestDto.count());
        verify(openRunProductRedisRepository, times(1)).increaseQuantity(product.getId(), orderRequestDto.count());
        verify(orderCreateProducer, never()).createOrder(any(OrderEventDto.class));

    }

    @Test
    public void testDeleteOrders() {
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
        when(orderRepository.findWithLockById(any(Long.class))).thenReturn(Optional.of(order));

        orderService.deleteOrders(order.getId(), member);

        verify(openRunProductRedisRepository, times(1)).increaseQuantity(order.getProduct().getId(), order.getCount());
        verify(orderRepository, times(1)).delete(any(Order.class));

    }

    @Test
    public void testDeleteOrders_NoPermission_ExceptionThrown() {
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
        when(orderRepository.findWithLockById(any(Long.class))).thenReturn(Optional.of(order));

        ResponseStatusException orderException = assertThrows(ResponseStatusException.class, () -> orderService.deleteOrders(order.getId(), member2));

        // then
        assertEquals("400 BAD_REQUEST \"권한이 없습니다. 사유 : 주문\"", orderException.getMessage());
    }

    @Test
    public void testDeleteOrders_OrderNotFound_ExceptionThrown() {
        // given
        Long orderId = 1L;

        Member member = Member.builder()
                .id(1L)
                .build();

        // when
        when(orderRepository.findWithLockById(any(Long.class))).thenReturn(Optional.empty());

        ResponseStatusException orderException = assertThrows(ResponseStatusException.class, () -> orderService.deleteOrders(orderId, member));

        // then
        assertEquals("400 BAD_REQUEST \"데이터가 존재하지 않습니다. 사유 : 주문\"", orderException.getMessage());
    }
}
