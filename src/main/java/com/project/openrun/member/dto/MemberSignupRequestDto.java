package com.project.openrun.member.dto;

import com.project.openrun.member.entity.MemberRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

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
