package com.project.openrun.wish.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.product.entity.Product;
import com.project.openrun.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long>, WishRepositoryCustom {

    Optional<Wish> findByProductAndMember(Product product, Member member);

}
