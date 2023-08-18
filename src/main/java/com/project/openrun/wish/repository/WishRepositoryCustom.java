package com.project.openrun.wish.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.wish.dto.WishProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishRepositoryCustom {
    Page<WishProductResponseDto> findAllByMemberOrderByIdDesc(Member member, Pageable pageable);
}
