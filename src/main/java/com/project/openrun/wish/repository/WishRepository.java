package com.project.openrun.wish.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.product.entity.Product;
import com.project.openrun.wish.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish,Long> {

    Optional<Wish> findByProductAndMember(Product product, Member member);

    Page<Wish> findAllByMember(Member member, Pageable pageable);
}
