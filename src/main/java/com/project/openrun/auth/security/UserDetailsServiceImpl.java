
package com.project.openrun.auth.security;

import com.project.openrun.member.entity.Member;
import com.project.openrun.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /* @RestControllerAdvice 에서 @ExceptionHandler 에서 잡힐까? filter는 advice 보다 더 앞쪽에 있다고 일단 판단됨.. */
        Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + email));

        return new UserDetailsImpl(member);
    }
}
