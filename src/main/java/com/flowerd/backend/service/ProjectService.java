package com.flowerd.backend.service;

import com.flowerd.backend.entity.Project;
import com.flowerd.backend.entity.dto.inbound.ProjectVO;
import com.flowerd.backend.entity.dto.outbound.AllReturns;
import com.flowerd.backend.entity.dto.outbound.ProjectDrawReturns;
import com.flowerd.backend.entity.dto.outbound.ProjectListReturns;
import com.flowerd.backend.entity.dto.outbound.ProjectReturns;
import com.flowerd.backend.repository.*;
import com.flowerd.backend.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final SchemaRepository schemaRepository;
    private final SchemaService schemaService;
    private final DiagramService diagramService;
    private final DiagramRepository diagramRepository;

    public Mono<ObjectId> saveProject(ProjectVO projectVO, String token) {
        return Mono.just(jwtTokenProvider.getEmailForAccessToken(token))
                .flatMap(memberRepository::findByEmail)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 멤버가 없습니다.")))
                .flatMap(member ->
                        projectRepository.save(
                                new Project(
                                        projectVO.getProjectName(),
                                        member.getId()
                                )
                        ).map(Project::getId)
                );
    }

    // 스키마 검색 및 삭제 -> 체이닝 돌면서 삭제해줌
    public Mono<Void> deleteProject(ObjectId projectId) {
        return schemaRepository.findAllByProjectId(projectId)
                .flatMap(schema -> schemaService.deleteSchema(schema.getId()))
                .then(projectRepository.deleteById(projectId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 프로젝트가 없습니다.")));
    }

    // 수정은 각 항목에서 합시다.
    public Mono<Void> updateProject(ProjectVO projectVO, ObjectId projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 프로젝트가 없습니다.")))
                .flatMap(project -> {
                    if (projectVO.getProjectName() != null) {
                        project.setProjectName(projectVO.getProjectName());
                    }
                    return projectRepository.save(project);
                }).then();
    }

    // 프로젝트의 모든것 -> 일단 스키마 리스트로 가져와
    public Mono<ProjectReturns> getListProject(ObjectId projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 프로젝트가 없습니다.")))
                .flatMap(project -> {
                    ProjectReturns projectReturns = new ProjectReturns(project.getId(), project.getProjectName(), null);

                    // SchemaRepository에서 projectId로 연결된 스키마들을 조회하여 스키마 ID 목록 추출
                    return schemaRepository.findAllByProjectId(projectId)
                            .flatMap(schema -> schemaService.getListSchema(schema.getId())) // 각 스키마 ID로 getListSchema 호출
                            .collectList()
                            .map(schemaReturnsList -> {
                                projectReturns.setSchemaReturns(schemaReturnsList);
                                return projectReturns;
                            });
                });
    }

    public  Mono<ProjectDrawReturns> getListDrawProject(ObjectId projectId){
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 프로젝트가 없습니다.")))
                .flatMap(project -> {
                    ProjectDrawReturns projectDrawReturns = new ProjectDrawReturns(project.getId(), project.getProjectName(), null);

                    // DiagramRepository에서 projectId로 연결된 다이어그램들을 조회하여 다이어그램 ID 목록 추출
                    return diagramRepository.findAllByProjectId(projectId)
                            .flatMap(diagram -> diagramService.getListDiagram(diagram.getId()))
                            .collectList()
                            .map(diagramReturnsList -> {
                                projectDrawReturns.setDiagramReturns(diagramReturnsList);
                                return projectDrawReturns;
                            });
                });
    }

    public Mono<AllReturns> getAllProject(ObjectId projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 프로젝트가 없습니다.")))
                .flatMap(project -> {
                    AllReturns allReturns = new AllReturns();
                    return getListProject(projectId)
                            .flatMap(projectReturns -> {
                                allReturns.setProjectReturns(List.of(projectReturns));
                                return getListDrawProject(projectId);
                            })
                            .map(projectDrawReturns -> {
                                allReturns.setProjectDrawReturns(List.of(projectDrawReturns));
                                return allReturns;
                            });
                });
    }

    public Mono<List<ProjectListReturns>> getProjectIdList(String token) {
        return memberRepository.findByEmail(jwtTokenProvider.getEmailForAccessToken(token))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 멤버가 없습니다.")))
                .flatMapMany(member ->
                        projectRepository.findAllByOwnerId(member.getId())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 프로젝트가 없습니다.")))
                )
                .map(project -> new ProjectListReturns(project.getId(),project.getProjectName())
                )
                .collectList();
    }


}
