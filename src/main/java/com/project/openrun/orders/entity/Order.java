package com.project.openrun.orders.entity;

import com.project.openrun.global.entity.BaseAuditing;
import com.project.openrun.member.entity.Member;
import com.project.openrun.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;


@Entity
@Getter
@Builder
@Table(name = "`orders`")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Integer totalPrice;


}
