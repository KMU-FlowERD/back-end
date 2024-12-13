package com.flowerd.backend.service;

import com.flowerd.backend.entity.Diagram;
import com.flowerd.backend.entity.dto.inbound.DiagramVO;
import com.flowerd.backend.entity.dto.outbound.DiagramReturns;
import com.flowerd.backend.repository.DiagramRepository;
import com.flowerd.backend.repository.DiagramTableRepository;
import com.flowerd.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DiagramService {

    private final DiagramRepository diagramRepository;
    private final DiagramTableRepository diagramTableRepository;
    private final TableService tableService;
    private final ProjectRepository projectRepository;

    // 다이어그램 생성시에는 테이블과 매핑 안됨
    public Mono<ObjectId> saveDiagram(DiagramVO diagramVO) {
        return projectRepository.findById(diagramVO.getProjectId())
                .switchIfEmpty(Mono.error(new RuntimeException("프로젝트를 찾을 수 없습니다.")))
                .flatMap(project ->
                        diagramRepository.save(
                            new Diagram(
                                    diagramVO.getProjectId(),
                                    diagramVO.getSchemaId(),
                                    diagramVO.getDiagramName(),
                                    diagramVO.getSizeX(),
                                    diagramVO.getSizeY(),
                                    diagramVO.getDiagramContent()
                            )
                        ).map(Diagram::getId)
                );
    }

    // DiagramTable 삭제 -> 체이닝 돌면서 삭제해줌.
    public Mono<Void> deleteDiagram(ObjectId diagramId) {
        return diagramTableRepository.findAllByDiagramId(diagramId)
                .flatMap(diagramTable -> diagramTableRepository.deleteById(diagramTable.getId()))
                .then(diagramRepository.deleteById(diagramId))
                .switchIfEmpty(Mono.error(new RuntimeException("다이어그램이 존재하지 않습니다.")));
    }

    public Mono<Void> updateDiagram(DiagramVO diagramVO, ObjectId diagramId) {
        return diagramRepository.findById(diagramId).
                switchIfEmpty(Mono.error(new RuntimeException("다이어그램이 존재하지 않습니다.")))
                .flatMap(diagram -> {
                    if(diagramVO.getDiagramName() != null)
                        diagram.setDiagramId(diagramVO.getDiagramName());
                    if(diagramVO.getSizeX() != null)
                        diagram.setPixel_x(diagramVO.getSizeX()); // maxPixel 조정
                    if(diagramVO.getSizeY() != null)
                        diagram.setPixel_y(diagramVO.getSizeY()); // maxPixel 조정
                    if(diagramVO.getDiagramContent() != null)
                        diagram.setDiagramContent(diagramVO.getDiagramContent());
                    return diagramRepository.save(diagram);
                }).then();
    }

    // 다이어그램 -> 다이어그램 테이블 검색 -> 테이블 모두 검색
    public Mono<DiagramReturns> getListDiagram(ObjectId diagramId) {
        return diagramRepository.findById(diagramId)
                .flatMap(diagram -> {
                    DiagramReturns diagramReturns = new DiagramReturns(diagram.getId(), diagram.getSchemaId(), diagram.getPixel_x(), diagram.getPixel_y(), diagram.getDiagramContent(), null);

                    return diagramTableRepository.findAllByDiagramId(diagramId)
                            .flatMap(diagramTable -> tableService.getListTableByDiagramTableId(diagramTable.getId()))
                            .collectList()
                            .map(diagramTableList -> {
                                diagramReturns.setTables(diagramTableList);
                                return diagramReturns;
                            });
                });
    }
}
