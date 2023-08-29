
package com.project.openrun.auth.security;

import com.project.openrun.member.entity.Member;
import com.project.openrun.member.repository.MemberRedisRepository;
import com.project.openrun.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberRedisRepository memberRedisRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /* @RestControllerAdvice 에서 @ExceptionHandler 에서 잡힐까? filter는 advice 보다 더 앞쪽에 있다고 일단 판단됨.. */
        Member member = memberRedisRepository.getMember(email).orElseGet(() ->{
            Member findMember = memberRepository.findByMemberEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Not Found " + email));

            memberRedisRepository.setMember(email, findMember);
            System.out.println("memberRedisRepository.setMember(email, findMember) 실행 -> redis에 세팅됨");
            return findMember;
        });

        return new UserDetailsImpl(member);
    }
}
