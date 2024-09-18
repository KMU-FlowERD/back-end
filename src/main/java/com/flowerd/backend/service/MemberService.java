package com.flowerd.backend.service;

import com.flowerd.backend.entity.Member;
import com.flowerd.backend.entity.Role;
import com.flowerd.backend.exception.NoCredException;
import com.flowerd.backend.exception.UsernameNotFoundException;
import com.flowerd.backend.repository.MemberRepository;
import com.flowerd.backend.security.util.MailProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService implements ReactiveUserDetailsService {

    private final MemberRepository memberRepository; // 비동기적으로 변경된 레포지토리
    private final MailProvider mailProvider; // 비동기적으로 변경된 MailProvider

    public Mono<Void> saveLocalMember(String name, String email, String password) {
        // 메일 인증이 확인되었는지 먼저 체크
        return mailProvider.checkAck(email)
                .flatMap(ack -> {
                    if (ack) {
                        List<Role> roles = List.of(Role.ROLE_USER);
                        Member member = Member.of(email, name, password, roles);
                        return memberRepository.save(member).then();
                    } else {
                        return Mono.error(new NoCredException());
                    }
                });
    }

    public Mono<Boolean> checkMemberEmail(String email) {
        // 이메일 중복 확인을 비동기로 처리
        return memberRepository.existsByEmail(email).map(exists -> !exists);
    }

    public Mono<Void> sendCertMail(String email) {
        // 메일 전송을 비동기적으로 처리
        return mailProvider.sendMail(email);
    }

    public Mono<Boolean> checkCertMail(String email, String uuid) {
        // 메일 인증을 비동기적으로 확인
        return mailProvider.checkMail(email, uuid);
    }

    // 인증시 사용자를 비동기적으로 검색하는 메소드
    public Mono<UserDetails> findByUsername(String username) {
        return memberRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)))
                .map(member -> {
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    for (Role role : member.getRoles()) {
                        authorities.add(new SimpleGrantedAuthority(role.name()));
                    }
                    return User.builder()
                            .username(member.getEmail())
                            .password(member.getPassword())
                            .authorities(authorities)
                            .build();
                });
    }
}

