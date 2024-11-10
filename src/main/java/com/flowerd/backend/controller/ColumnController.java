package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.inbound.ColumnVO;
import com.flowerd.backend.entity.dto.outbound.ColumnReturns;
import com.flowerd.backend.service.ColumnService;
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
public class ColumnController {

        private final ColumnService columnService;

        // 새로운 컬럼 추가.
        @PostMapping("/add/column")
        @Operation(summary = "컬럼 추가", description = "새로운 컬럼을 추가합니다.")
        public Mono<ResponseEntity<ApiResponse<String>>> saveColumn(@RequestBody ColumnVO columnVO) {
                return columnService.saveColumn(columnVO)
                        .map(id -> ResponseEntity.ok(ApiResponse.success(id.toString())))
                        .onErrorResume(e -> {
                                log.error("컬럼 추가 실패: {}", e.getMessage());
                                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("컬럼 추가 실패: " + e.getMessage())));
                        });
        }

        // 컬럼 수정.
        @PutMapping("/update/column/{columnId}")
        @Operation(summary = "컬럼 수정", description = "새로운 컬럼을 수정합니다.")
        public Mono<ResponseEntity<ApiResponse<Object>>> updateColumn(@RequestBody ColumnVO columnVO, @PathVariable ObjectId columnId) {
                return columnService.updateColumn(columnVO, columnId)
                        .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                        .onErrorResume(e -> {
                                log.error("컬럼 수정 실패: {}", e.getMessage());
                                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("컬럼 수정 실패: " + e.getMessage())));
                        });
        }


        // 컬럼 삭제.
        @DeleteMapping("/delete/column/{columnId}")
        @Operation(summary = "컬럼 삭제", description = "컬럼을 삭제합니다.")
        public Mono<ResponseEntity<ApiResponse<Object>>> deleteColumn(@PathVariable ObjectId columnId) {
                return columnService.deleteColumn(columnId)
                        .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                        .onErrorResume(e -> {
                                log.error("컬럼 삭제 실패: {}", e.getMessage());
                                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("컬럼 삭제 실패: " + e.getMessage())));
                        });
        }


        // 컬럼 조회.
        @GetMapping("/get/column/{columnId}")
        @Operation(summary = "컬럼 조회", description = "컬럼을 조회합니다.")
        public Mono<ResponseEntity<ApiResponse<ColumnReturns>>> getColumn(@PathVariable ObjectId columnId) {
                return columnService.getListColumn(columnId)
                        .map(column -> ResponseEntity.ok(ApiResponse.success(column)))
                        .onErrorResume(e -> {
                                log.error("컬럼 조회 실패: {}", e.getMessage());
                                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("컬럼 조회 실패: " + e.getMessage())));
                        });
        }

}
