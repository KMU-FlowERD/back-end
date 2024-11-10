package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.inbound.ConstraintsVO;
import com.flowerd.backend.entity.dto.outbound.ConstraintsReturns;
import com.flowerd.backend.service.ConstraintsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConstraintsController {

    private final ConstraintsService constraintsService;

    // 새로운 제약조건 추가
    @PostMapping("/add/constraints")
    @Operation(summary = "제약조건 추가", description = "새로운 제약조건을 추가합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> saveConstraints(@RequestBody ConstraintsVO constraintsVO) {
        return constraintsService.saveConstraints(constraintsVO)
                .map(id -> ResponseEntity.ok(ApiResponse.success(id.toString())))
                .onErrorResume(e -> {
                    log.error("제약조건 추가 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("제약조건 추가 실패: " + e.getMessage())));
                });
    }


    // 제약조건 수정
    @PutMapping("/update/constraints/{constraintsId}")
    @Operation(summary = "제약조건 수정", description = "제약조건을 수정합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> updateConstraints(@RequestBody ConstraintsVO constraintsVO, @PathVariable ObjectId constraintsId) {
        return constraintsService.updateConstraints(constraintsVO, constraintsId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("제약조건 수정 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("제약조건 수정 실패: " + e.getMessage())));
                });
    }


    // 제약조건 삭제.
    @DeleteMapping("/delete/constraints/{constraintsId}")
    @Operation(summary = "제약조건 삭제", description = "제약조건을 삭제합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteConstraints(@PathVariable ObjectId constraintsId) {
        return constraintsService.deleteConstraints(constraintsId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("제약조건 삭제 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("제약조건 삭제 실패: " + e.getMessage())));
                });
    }


    // 제약조건 조회
    @GetMapping("/get/constraints/{constraintsId}")
    @Operation(summary = "제약조건 조회", description = "제약조건을 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<ConstraintsReturns>>> getConstraints(@PathVariable ObjectId constraintsId) {
        return constraintsService.getListConstraints(constraintsId)
                .map(constraints -> ResponseEntity.ok(ApiResponse.success(constraints)))
                .onErrorResume(e -> {
                    log.error("제약조건 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("제약조건 조회 실패: " + e.getMessage())));
                });
    }
}
