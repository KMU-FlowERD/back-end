package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.inbound.DiagramTableVO;
import com.flowerd.backend.entity.dto.inbound.TableVO;
import com.flowerd.backend.entity.dto.outbound.TableReturns;
import com.flowerd.backend.service.TableService;
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
public class TableController {

    private final TableService tableService;

    // 새로운 테이블 추가 & 수정.
    @PostMapping("/add/table")
    @Operation(summary = "테이블 추가", description = "새로운 테이블을 추가합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> saveTable(@RequestBody TableVO tableVO) {
        return tableService.saveTable(tableVO)
                .map(id -> ResponseEntity.ok(ApiResponse.success(id.toString())))
                .onErrorResume(e -> {
                    log.error("테이블 추가 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("테이블 추가 실패: " + e.getMessage())));
                });
    }

    // 테이블 삭제.
    @DeleteMapping("/delete/table/{tableId}")
    @Operation(summary = "테이블 삭제", description = "테이블을 삭제합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteTable(@PathVariable ObjectId tableId) {
        return tableService.deleteTable(tableId)
                .then(Mono.fromCallable(() -> ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("테이블 삭제 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("테이블 삭제 실패: " + e.getMessage())));
                });
    }

    // 테이블 수정
    @PutMapping("/update/table/{tableId}")
    @Operation(summary = "테이블 수정", description = "테이블을 수정합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> updateTable(@RequestBody TableVO tableVO, @PathVariable ObjectId tableId) {
        return tableService.updateTable(tableVO, tableId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("테이블 수정 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("테이블 수정 실패: " + e.getMessage())));
                });
    }

    // 테이블 조회.
    @GetMapping("/get/table/{tableId}")
    @Operation(summary = "테이블 조회", description = "테이블을 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<TableReturns>>> getTable(@PathVariable ObjectId tableId) {
        return tableService.getListTableByTableId(tableId)
                .map(table -> ResponseEntity.ok(ApiResponse.success(table)))
                .onErrorResume(e -> {
                    log.error("테이블 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("테이블 조회 실패: " + e.getMessage())));
                });
    }

    @PostMapping("/diagramTable/{tableId}/{diagramId}")
    @Operation(summary = "다이어그램 테이블 매핑", description = "다이어그램과 테이블을 매핑합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> saveDiagramTable(@PathVariable ObjectId tableId, @PathVariable ObjectId diagramId, @RequestBody DiagramTableVO diagramTableVO) {
        return tableService.mapTableToDiagram(tableId, diagramId, diagramTableVO)
                .map(id -> ResponseEntity.ok(ApiResponse.success(id.toString())))
                .onErrorResume(e -> {
                    log.error("다이어그램 테이블 매핑 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("다이어그램 테이블 매핑 실패: " + e.getMessage())));
                });
    }

    @DeleteMapping("/diagramTable/{tableId}/{diagramId}")
    @Operation(summary = "다이어그램 테이블 매핑 삭제", description = "다이어그램과 테이블의 매핑을 삭제합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteDiagramTable(@PathVariable ObjectId tableId, @PathVariable ObjectId diagramId) {
        return tableService.unmapTableToDiagram(tableId, diagramId)
                .then(Mono.fromCallable(() -> ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("다이어그램 테이블 매핑 삭제 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("다이어그램 테이블 매핑 삭제 실패: " + e.getMessage())));
                });
    }
}
