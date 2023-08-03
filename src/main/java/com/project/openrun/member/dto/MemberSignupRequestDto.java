package com.project.openrun.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
public class MemberSignupRequestDto {

    @NotBlank
    private String membername;
    @NotBlank
    private String memberpassword;

    @Email
    @NotBlank
    private String memberemail;

    //private MemberRoleEnum memberRole;



}
