package com.project.openrun.wish.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.product.entity.Product;
import com.project.openrun.wish.dto.WishProductResponseDto;
import com.project.openrun.wish.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish,Long>, WishRepositoryCustom {

    /*@Query (value =  "select new com.project.openrun.wish.dto.WishProductResponseDto(w.id, w.product, w., w.mallName, w.productImage)from Wish w left join fetch w.product p where w.member = :member order by o.createdAt desc")*/
    Optional<Wish> findByProductAndMember(Product product, Member member);

//    @Query("select new com.project.openrun.wish.dto.WishProductResponseDtzo(p.id, p.productName,p.price,p.mallName,p.productImage ) from Wish w left join w.product p where w.member = :member order by w.id desc")
//    Page<WishProductResponseDto> findAllByMemberOrderByIdDesc(Member member, Pageable pageable);
}
