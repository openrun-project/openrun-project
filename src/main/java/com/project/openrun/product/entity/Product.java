package com.project.openrun.product.entity;

import com.project.openrun.global.entity.BaseAuditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product extends BaseAuditing {

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

    private Integer wishCount;

    @Enumerated(EnumType.STRING)
    private OpenRunStatus status;

    public void decreaseQuantity(Integer count) {
        if (this.getCurrentQuantity() < count) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다.");
        }
        this.currentQuantity -= count;
    }

    public void increaseQuantity(Integer count) {
        this.currentQuantity += count;
    }

    public void addWish() {
        this.wishCount += 1;
    }

    public void deleteWish() {
        this.wishCount -= 1;
    }
}
