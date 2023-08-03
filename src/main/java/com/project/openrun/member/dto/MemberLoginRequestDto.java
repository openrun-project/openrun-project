package com.project.openrun.member.dto;

public record MemberLoginRequestDto(
        String memberemail,
        String memberpassword
) {}