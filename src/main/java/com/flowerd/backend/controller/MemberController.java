package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.JwtTokenResponse;
import com.flowerd.backend.entity.dto.MailCertRequest;
import com.flowerd.backend.entity.dto.MemberRequest;
import com.flowerd.backend.security.util.CookieProvider;
import com.flowerd.backend.service.MemberService;
import com.flowerd.backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final CookieProvider cookieProvider;
    private final TokenService tokenService;

    @PostMapping("/register/member/local")
    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> registerLocalMember(@RequestBody MemberRequest request) {
        return memberService.saveLocalMember(request.name(), request.email(), passwordEncoder.encode(request.password()))
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."))))
                .onErrorResume(e -> {
                    log .error("회원가입 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("회원가입 실패: " + e.getMessage())));
                });
    }

    // 중복검사
    @GetMapping("/duplicate/email/{email}")
    @Operation(summary = "이메일 중복검사", description = "이메일 중복검사를 진행합니다.")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> checkMemberEmail(@PathVariable String email) {
        return memberService.checkMemberEmail(email)
                .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
                .onErrorResume(e -> {
                    log.error("중복 검사 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("중복 검사 실패: " + e.getMessage())));
                });
    }

    // 메일인증 전송요청
    @GetMapping("/send/email/{email}")
    @Operation(summary = "메일인증 전송", description = "메일인증을 위한 이메일을 전송합니다.")
    public Mono<Void> sendCertMail(@PathVariable String email) {
        return memberService.sendCertMail(email)
                .onErrorResume(e -> {
                    log.error("메일 전송 실패: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    // 메일인증 확인
    @PostMapping("/check/mail")
    @Operation(summary = "메일인증 확인", description = "메일인증을 확인합니다.")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> checkCertMail(@RequestBody MailCertRequest request) {
        return memberService.checkCertMail(request.email(), request.uuid())
                .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
                .onErrorResume(e -> {
                    log.error("메일 인증 확인 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("메일 인증 확인 실패: " + e.getMessage())));
                });
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> logout(@RequestHeader("Authorization") String accessToken) {
        return tokenService.logout(accessToken)
                .then(Mono.defer(() -> {
                    ResponseCookie responseCookie = cookieProvider.deleteRefreshCookie();
                    return Mono.just(ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                            .body(ApiResponse.success("로그아웃 되었습니다.")));
                }))
                .onErrorResume(e -> {
                    log.error("로그아웃 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("로그아웃 실패: " + e.getMessage())));
                });
    }

    @GetMapping("/reissue")
    @Operation(summary = "AccessToken 재발급", description = "AccessToken을 재발급합니다.")
    public Mono<ResponseEntity<ApiResponse<JwtTokenResponse>>> reissue(@RequestHeader("Authorization") String accessToken,
                                                                       @CookieValue("RefreshToken") String refreshToken) {
        return tokenService.updateAccessToken(accessToken, refreshToken)
                .map(jwtTokenResponse -> {
                    ResponseCookie responseCookie = cookieProvider.createRefreshCookie(jwtTokenResponse.refreshToken());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                            .body(ApiResponse.success(jwtTokenResponse));
                })
                .onErrorResume(e -> {
                    log.error("토큰 재발급 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("토큰 재발급 실패: " + e.getMessage())));
                });
    }
}

