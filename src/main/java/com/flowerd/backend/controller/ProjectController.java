package com.flowerd.backend.controller;

import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.inbound.ProjectVO;
import com.flowerd.backend.entity.dto.outbound.AllReturns;
import com.flowerd.backend.entity.dto.outbound.ProjectDrawReturns;
import com.flowerd.backend.entity.dto.outbound.ProjectListReturns;
import com.flowerd.backend.entity.dto.outbound.ProjectReturns;
import com.flowerd.backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    // Admin
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    // 새로운 프로젝트 추가.
    @PostMapping("/add/project")
    @Operation(summary = "프로젝트 추가", description = "새로운 프로젝트를 추가합니다.")
    public Mono<ResponseEntity<ApiResponse<String>>> addProject(@RequestBody ProjectVO project, @RequestHeader("Authorization") String token) {
        return projectService.saveProject(project, token)
                .map(id -> ResponseEntity.ok(ApiResponse.success(id.toString())))
                .onErrorResume(e -> {
                    log.error("프로젝트 추가 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("프로젝트 추가 실패: " + e.getMessage())));
                });
    }

    // All Project 리스트 조회
    @GetMapping("/get/project/all/{projectId}")
    @Operation(summary = "프로젝트 All 조회", description = "모든 프로젝트 리스트를 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<AllReturns>>> getAllProject(@PathVariable ObjectId projectId) {
        return projectService.getAllProject(projectId)
                .map(allProject -> ResponseEntity.ok(ApiResponse.success(allProject)))
                .onErrorResume(e -> {
                    log.error("프로젝트 리스트 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("프로젝트 리스트 조회 실패: " + e.getMessage())));
                });
    }


    // Project 조회
    @GetMapping("/get/project/{projectId}")
    @Operation(summary = "프로젝트 조회", description = "프로젝트를 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<ProjectReturns>>> getProject(@PathVariable ObjectId projectId) {
        return projectService.getListProject(projectId)
                .map(project -> ResponseEntity.ok(ApiResponse.success(project)))
                .onErrorResume(e -> {
                    log.error("프로젝트 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("프로젝트 조회 실패: " + e.getMessage())));
                });
    }

    // ProjecrDraw 조회
    @GetMapping("/get/project/draw/{projectId}")
    @Operation(summary = "프로젝트 Draw 조회", description = "프로젝트 Draw를 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<ProjectDrawReturns>>> getProjectDraw(@PathVariable ObjectId projectId) {
        return projectService.getListDrawProject(projectId)
                .map(project -> ResponseEntity.ok(ApiResponse.success(project)))
                .onErrorResume(e -> {
                    log.error("프로젝트 Draw 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("프로젝트 Draw 조회 실패: " + e.getMessage())));
                });
    }

    // 프로젝트 수정
    @PutMapping("/update/project/{projectId}")
    @Operation(summary = "프로젝트 수정", description = "프로젝트를 수정합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> updateProject(@RequestBody ProjectVO project, @PathVariable ObjectId projectId) {
        return projectService.updateProject(project, projectId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("프로젝트 수정 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("프로젝트 수정 실패: " + e.getMessage())));
                });
    }

    // 프로젝트 삭제
    @DeleteMapping("/delete/project/{projectId}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteProject(@PathVariable ObjectId projectId) {
        return projectService.deleteProject(projectId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.success())))
                .onErrorResume(e -> {
                    log.error("프로젝트 삭제 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("프로젝트 삭제 실패: " + e.getMessage())));
                });
    }

    // 프로젝트 리스트
    @GetMapping("/get/project/list")
    @Operation(summary = "프로젝트 리스트", description = "프로젝트 리스트를 조회합니다.")
    public Mono<ResponseEntity<ApiResponse<List<ProjectListReturns>>>> getProjectList(@RequestHeader("Authorization") String token){
        return projectService.getProjectIdList(token)
                .map(projectList -> ResponseEntity.ok(ApiResponse.success(projectList)))
                .onErrorResume(e -> {
                    log.error("프로젝트 리스트 조회 실패: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.fail("프로젝트 리스트 조회 실패: " + e.getMessage())));
                });
    }

    // 어드민로직
    @DeleteMapping("refresh_All_Database")
    @Operation(summary = "데이터베이스 초기화", description = "데이터베이스를 초기화합니다.")
    public void refreshAllDatabase() {
        Mono<Void> initProcess = reactiveMongoTemplate.dropCollection("table")
					.then(reactiveMongoTemplate.dropCollection("column"))
					.then(reactiveMongoTemplate.dropCollection("constraints"))
					.then(reactiveMongoTemplate.dropCollection("diagram_table"))
					.then(reactiveMongoTemplate.dropCollection("schema"))
					.then(reactiveMongoTemplate.dropCollection("diagram"))
					.then(reactiveMongoTemplate.dropCollection("project"));

        initProcess
                .doOnSuccess(unused -> System.out.println("모든 컬렉션이 성공적으로 삭제되었습니다."))
                .doOnError(error -> System.err.println("컬렉션 삭제 중 오류 발생: " + error.getMessage()))
                .subscribe();
    }
}
