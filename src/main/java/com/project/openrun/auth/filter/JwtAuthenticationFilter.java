package com.project.openrun.auth.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.openrun.auth.jwt.JwtUtil;
import com.project.openrun.auth.security.UserDetailsImpl;
import com.project.openrun.member.dto.MemberLoginRequestDto;
import com.project.openrun.member.entity.MemberRoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/members/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[JwtAuthenticationFilter attemptAuthentication] 동작");
        try {
            MemberLoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), MemberLoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.memberemail(),
                            requestDto.memberpassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        MemberRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getMember().getMemberRole();

        String token = jwtUtil.createToken(email, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        response.setStatus(HttpServletResponse.SC_OK);
        writeJsonResponse(response, "성공");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(401);
        writeJsonResponse(response, "실패");
    }

    private void writeJsonResponse(HttpServletResponse response, String msg) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = "{\"msg\": \"" + msg + "\"}";

        PrintWriter writer = response.getWriter();
        writer.print(json);
        writer.flush();
    }
}