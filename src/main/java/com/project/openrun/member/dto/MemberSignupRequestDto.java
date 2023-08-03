package com.project.openrun.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberSignupRequestDto(
        @NotBlank String membername,
        @NotBlank String memberpassword,
        @Email @NotBlank String memberemail
) {
}
