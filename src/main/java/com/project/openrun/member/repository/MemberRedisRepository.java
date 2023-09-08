package com.project.openrun.member.repository;


import com.project.openrun.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRedisRepository {

    private final RedisTemplate<String, Member> memberRedisTemplate;
    private static final long MEMBER_TTL = 3 * 60L;
    public void setMember(String email, Member member) {
        memberRedisTemplate.opsForValue().set(createKey(email), member, Duration.ofSeconds(MEMBER_TTL));
    }

    public Optional<Member> getMember(String email) {
        return Optional.ofNullable(memberRedisTemplate.opsForValue().get(createKey(email)));
    }

    private String createKey(String email) {
        return "USER:" + email;
    }
}
