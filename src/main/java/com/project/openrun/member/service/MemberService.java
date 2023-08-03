package com.project.openrun.member.service;

import com.project.openrun.global.exception.MemberException;
import com.project.openrun.global.exception.type.MemberErrorCode;
import com.project.openrun.member.dto.MemberSignupRequestDto;
import com.project.openrun.member.entity.Member;
import com.project.openrun.member.entity.MemberRoleEnum;
import com.project.openrun.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(MemberSignupRequestDto memberSignupRequestDto) {
        String email = memberSignupRequestDto.memberemail();
        String password = passwordEncoder.encode(memberSignupRequestDto.memberpassword());

        if(memberRepository.findByMemberEmail(email).isPresent()){
            throw new MemberException(MemberErrorCode.DUPLICATE_EMAIL);
        }

        Member member = Member.builder()
                .memberEmail(email)
                .memberName(memberSignupRequestDto.membername())
                .memberPassword(password)
                .memberRole(MemberRoleEnum.USER)
                .build();

        memberRepository.save(member);
    }

}
