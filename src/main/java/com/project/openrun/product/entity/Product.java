package com.project.openrun.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String productName;
    private String productImage;
    private Integer price;
    private String mallName;
    private Integer currentQuantity;
    private LocalDateTime eventStartTime;
    private String category;
    private Integer totalQuantity;

    private Integer wishCount = 0;

}
