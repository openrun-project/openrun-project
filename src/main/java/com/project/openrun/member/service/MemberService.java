package com.project.openrun.member.service;

import com.project.openrun.member.dto.MemberLoginRequestDto;
import com.project.openrun.member.dto.MemberSignupRequestDto;
import com.project.openrun.member.entity.Member;
import com.project.openrun.member.entity.MemberRoleEnum;
import com.project.openrun.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(MemberSignupRequestDto memberSignupRequestDto) {
        String email = memberSignupRequestDto.getMemberemail();
        String password = passwordEncoder.encode(memberSignupRequestDto.getMemberpassword());

        if(memberRepository.findByMemberEmail(email).isPresent()){
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        Member member = Member.builder()
                .memberEmail(email)
                .memberName(memberSignupRequestDto.getMembername())
                .memberPassword(password)
                .memberRole(MemberRoleEnum.USER)
                .build();

        memberRepository.save(member);
    }

}
