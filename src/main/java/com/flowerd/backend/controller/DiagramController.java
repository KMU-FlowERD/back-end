package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.inbound.DiagramVO;
import com.flowerd.backend.entity.dto.outbound.DiagramReturns;
import com.flowerd.backend.service.DiagramService;
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
public class DiagramController {

    private final DiagramService diagramService;

    // 다이어그램 추가
    @PostMapping("/add/diagram")
    @Operation(summary = "다이어그램 추가", description = "새로운 다이어그램을 추가합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> addDiagram(@RequestBody DiagramVO diagramVO) {
        return diagramService.saveDiagram(diagramVO)
                .map(id -> ResponseEntity.ok(ApiResponse.success(id.toString())))
                .onErrorResume(e -> {
                    log.error("다이어그램 추가 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("다이어그램 추가 실패: " + e.getMessage())));
                });
    }

    // 다이어그램 조회
    @GetMapping("/get/diagram/{diagramId}")
    @Operation(summary = "다이어그램 조회", description = "다이어그램을 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<DiagramReturns>>> getDiagram(@PathVariable ObjectId diagramId) {
        return diagramService.getListDiagram(diagramId)
                .map(diagram -> ResponseEntity.ok(ApiResponse.success(diagram)))
                .onErrorResume(e -> {
                    log.error("다이어그램 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("다이어그램 조회 실패: " + e.getMessage())));
                });
    }

    // 다이어그램 수정
    @PutMapping("/update/diagram/{diagramId}")
    @Operation(summary = "다이어그램 수정", description = "다이어그램을 수정합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> updateDiagram(@RequestBody DiagramVO diagramVO, @PathVariable ObjectId diagramId) {
        return diagramService.updateDiagram(diagramVO, diagramId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("다이어그램 수정 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("다이어그램 수정 실패: " + e.getMessage())));
                });
    }

    // 다이어그램 삭제
    @DeleteMapping("/delete/diagram/{diagramId}")
    @Operation(summary = "다이어그램 삭제", description = "다이어그램을 삭제합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteDiagram(@PathVariable ObjectId diagramId) {
        return diagramService.deleteDiagram(diagramId)
                .then(Mono.fromCallable(() -> ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("다이어그램 삭제 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("다이어그램 삭제 실패: " + e.getMessage())));
                });
    }
}
