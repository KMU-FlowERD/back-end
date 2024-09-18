package com.flowerd.backend.service;

import com.flowerd.backend.entity.RefreshToken;
import com.flowerd.backend.entity.dto.JwtTokenResponse;
import com.flowerd.backend.exception.CustomTokenException;
import com.flowerd.backend.exception.UsernameNotFoundException;
import com.flowerd.backend.repository.MemberRepository;
import com.flowerd.backend.repository.RefreshTokenRedisRepository;
import com.flowerd.backend.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final MemberRepository memberRepository;  // 리액티브 리포지토리로 변경
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisRepository refreshTokenRepository;  // 리액티브 리포지토리로 변경

    public Mono<Void> updateRefreshToken(String email, String uuid) {
        return memberRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        return refreshTokenRepository.save(RefreshToken.of(email, uuid)).then();
                    } else {
                        return Mono.error(new UsernameNotFoundException("해당 이메일을 가진 사용자가 존재하지 않습니다."));
                    }
                });
    }

    public Mono<JwtTokenResponse> updateAccessToken(String accessToken, String refreshToken) {
        String email = jwtTokenProvider.getEmailForAccessToken(accessToken);

        return memberRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("해당 이메일을 가진 사용자가 존재하지 않습니다.")))
                .flatMap(member -> refreshTokenRepository.findById(member.getEmail())
                        .switchIfEmpty(Mono.error(new CustomTokenException("해당 이메일을 가진 사용자의 RefreshToken이 존재하지 않습니다.")))
                        .flatMap(refreshTokenEntity -> {
                            jwtTokenProvider.validateToken(refreshToken);

                            if (!jwtTokenProvider.sameRefreshToken(refreshToken, refreshTokenEntity.getRefreshTokenId())) {
                                return Mono.error(new CustomTokenException("RefreshToken이 일치하지 않습니다."));
                            }

                            List<String> roles = jwtTokenProvider.getRole(accessToken);
                            String newAccessToken = jwtTokenProvider.createAccessToken(member.getEmail(), roles);

                            return Mono.just(JwtTokenResponse.toResponse(newAccessToken, refreshToken));
                        })
                );
    }

    public Mono<Boolean> logout(String accessToken) {
        return Mono.just(jwtTokenProvider.validateToken(accessToken))
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new CustomTokenException("토큰이 유효하지 않습니다."));
                    }
                    return refreshTokenRepository.findById(jwtTokenProvider.getEmailForAccessToken(accessToken))
                            .switchIfEmpty(Mono.error(new CustomTokenException("해당 이메일을 가진 사용자의 RefreshToken이 존재하지 않습니다.")))
                            .flatMap(refreshTokenRepository::delete);
                });
    }
}

