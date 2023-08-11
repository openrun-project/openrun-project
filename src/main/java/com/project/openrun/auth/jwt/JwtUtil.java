package com.project.openrun.auth.jwt;


import com.project.openrun.member.entity.MemberRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    //JWT에서 사용하는 상수모음
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
                                        //  일   시간  분   초
    private final long TOKEN_EXPIRED_TIME = 60 * 24 * 60 * 60 * 1000L;

    @Value("${jwt.secretKey}") // Base64 Encode
    private String secretKey;

    //jwt secret key를 decode하여 저장하고 있는 key 세팅
    //security.key 라네?
    private Key key;
    //암호화에 사용된 알고리즘
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;



    //해당 클래스가 만들어질 때, 일종의 생성자처럼 활용되는 것
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    //JWT 생성
    //UserNamePassword
    public String createToken(String email, MemberRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(email)
//                .setClaims(claims)
                .claim(AUTHORIZATION_HEADER, role) // payload(body 정보)
                .setExpiration(new Date(date.getTime() + TOKEN_EXPIRED_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    //로그인시,
    //JWT 토큰을 Cookie에 저장
    public void addJwtToCookie(String token, HttpServletResponse response) {
        try {
            // 쿠키벨류값의 공백을 지워주기 위함
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);

            cookie.setMaxAge((int) (TOKEN_EXPIRED_TIME));

            response.addCookie(cookie); // 쿠키에 넣기

        } catch (UnsupportedEncodingException e) {
            log.error("쿠키 세팅 추가 실패");
            log.error(e.getMessage());
        }
    }

    public void addTwtToHeader(String token, HttpServletResponse response) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
            response.setHeader(AUTHORIZATION_HEADER, token);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    //검증 시,
    // Cookie에 들어있던, JWT 토큰을 Substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        log.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            throw new RuntimeException("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            throw new RuntimeException("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new RuntimeException("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new RuntimeException("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
    }

    //JWT 에서 사용자 정보 가지고 오기
    // body 정보이므로 getBody()로 꺼내옴
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token).getBody();
    }

    // JWT 에서 사용자 정보 가지고 오기 - HttpServletRequest 객체에서 가지고 오기
    public String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        // 뭔가 담겨 있어야 한다.
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        try {

            String token = request.getHeader(AUTHORIZATION_HEADER);
            if (token != null) {

                token = URLDecoder.decode(token, "utf-8");

                String tokenValue = substringToken(token);

                return tokenValue;
            }
        }catch(UnsupportedEncodingException e){
            return null;
        }
        return null;
    }
}
