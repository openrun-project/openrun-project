//package com.project.openrun.global.util;
//
//import com.project.openrun.auth.jwt.JwtUtil;
//import com.project.openrun.member.dto.MemberSignupRequestDto;
//import com.project.openrun.member.entity.MemberRoleEnum;
//import com.project.openrun.member.service.MemberService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class MemberCreateRunner implements ApplicationRunner {
//
//    private final MemberService memberService;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//
//        int createMemberNumber = 1000;
//
//        for (int i = 518; i < createMemberNumber; i++) {
//
//            memberService.signup(new MemberSignupRequestDto(
//                    "trafictest" + i,
//                    passwordEncoder.encode("abcd1234"),
//                    "trafictest" + i + "@naver.com"
//            ));
//
//        }
//
//        for (int i = 0; i < createMemberNumber; i++) {
//            String token = jwtUtil.createToken("trafictest" + i + "@naver.com", MemberRoleEnum.USER);
//            System.out.println("token = " + token);
//        }
//
//    }
//}
