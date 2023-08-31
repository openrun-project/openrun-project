
package com.project.openrun.auth.security;

import com.project.openrun.member.entity.Member;
import com.project.openrun.member.repository.MemberRedisRepository;
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
    private final MemberRedisRepository memberRedisRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRedisRepository.getMember(email).orElseGet(() ->{
            Member findMember = memberRepository.findByMemberEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Not Found " + email));

            memberRedisRepository.setMember(email, findMember);
            return findMember;
        });

        return new UserDetailsImpl(member);
    }
}
