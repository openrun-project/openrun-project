package com.project.openrun.member.controller;

import com.project.openrun.member.dto.MemberSignupRequestDto;
import com.project.openrun.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody MemberSignupRequestDto memberSignupRequestDto){
        memberService.signup(memberSignupRequestDto);
    }

}
