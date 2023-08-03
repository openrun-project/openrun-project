package com.project.openrun.member.dto;

import lombok.Getter;


public record MemberLoginRequestDto(
        String memberemail,
        String memberpassword
) {}