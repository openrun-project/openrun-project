package com.project.openrun.product.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime eventStartTime;

    private String category;

    private Integer totalQuantity;

    private Integer wishCount;

    @Enumerated(EnumType.STRING)
    private OpenRunStatus status;

    public void addWish() {
        this.wishCount += 1;
    }

    public void deleteWish() {
        this.wishCount -= 1;
    }


    public void decreaseProductQuantity(Integer count) {
        this.currentQuantity -= count;
    }
}
