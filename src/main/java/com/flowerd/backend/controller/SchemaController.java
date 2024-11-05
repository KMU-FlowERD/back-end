package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.inbound.SchemaVO;
import com.flowerd.backend.entity.dto.outbound.SchemaReturns;
import com.flowerd.backend.service.SchemaService;
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
public class SchemaController {

    private final SchemaService schemaService;

    @PostMapping("/add/schema")
    @Operation(summary = "스키마 추가", description = "새로운 스키마를 추가합니다.")
    public Mono<ResponseEntity<ApiResponse<ObjectId>>> addSchema(@RequestBody SchemaVO schema) {
        return schemaService.saveSchema(schema)
                .map(id -> ResponseEntity.ok(ApiResponse.success(id)))
                .onErrorResume(e -> {
                    log.error("스키마 추가 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("스키마 추가 실패: " + e.getMessage())));
                });
    }

    @GetMapping("/get/schema/{projectId}")
    @Operation(summary = "스키마 조회", description = "스키마를 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<SchemaReturns>>> getSchema(@PathVariable ObjectId projectId) {
        return schemaService.getListSchema(projectId)
                .map(schema -> ResponseEntity.ok(ApiResponse.success(schema)))
                .onErrorResume(e -> {
                    log.error("스키마 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("스키마 조회 실패: " + e.getMessage())));
                });
    }


    @DeleteMapping("/delete/schema/{schemaId}")
    @Operation(summary = "스키마 삭제", description = "스키마를 삭제합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteSchema(@PathVariable ObjectId schemaId) {
        return schemaService.deleteSchema(schemaId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("스키마 삭제 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("스키마 삭제 실패: " + e.getMessage())));
                });
    }


    @PutMapping("/update/schema/{schemaId}")
    @Operation(summary = "스키마 수정", description = "스키마를 수정합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> updateSchema(@RequestBody SchemaVO schema, @PathVariable ObjectId schemaId) {
        return schemaService.updateSchema(schema, schemaId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("스키마 수정 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("스키마 수정 실패: " + e.getMessage())));
                });
    }
}
