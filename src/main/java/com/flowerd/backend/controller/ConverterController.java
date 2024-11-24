package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.service.ConvertService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConverterController {
    private final ConvertService converterService;


    @GetMapping("/convert/{projectId}")
    @Operation(summary = "DDL 생성", description = "프로젝트의 DDL을 생성합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> convert(@PathVariable ObjectId projectId){
        return converterService.generateDDL(projectId)
                .map(ddl -> ResponseEntity.ok(ApiResponse.success(ddl)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("DDL 생성 실패: " + e.getMessage()))));
    }
}
