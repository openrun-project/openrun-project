package com.project.openrun.member.service;

import com.project.openrun.global.exception.CustomExceptionHandler;
import com.project.openrun.member.dto.MemberSignupRequestDto;
import com.project.openrun.member.entity.Member;
import com.project.openrun.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess(){

        //Given
        MemberSignupRequestDto memberSignupRequestDto = new MemberSignupRequestDto("이름", "비밀번호", "이메일@naver.com");

        when(memberRepository.findByMemberEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("비밀번호");

        //When
        memberService.signup(memberSignupRequestDto);

        //Then
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(memberRepository, times(1)).save(argThat(memberTest ->
                memberTest.getMemberEmail().equals("이메일@naver.com") &&
                        memberTest.getMemberName().equals("이름") &&
                        memberTest.getMemberPassword().equals("비밀번호")
        ));
    }

    @Test
    @DisplayName("회원가입 실패")
    void signUpFailed(){

        //Given
        MemberSignupRequestDto memberSignupRequestDto = new MemberSignupRequestDto("이름", "비밀번호", "이메일@naver.com");
        Member member = Member.builder().build();

        when(memberRepository.findByMemberEmail(anyString())).thenReturn(Optional.of(Member.builder().build()));
        when(passwordEncoder.encode(anyString())).thenReturn("비밀번호");

        //When
        assertThrows(ResponseStatusException.class, () -> memberService.signup(memberSignupRequestDto));

        //Then
        verify(memberRepository, never()).save(any(Member.class));
    }

}

