package com.flowerd.backend.service;

import com.flowerd.backend.entity.Schema;
import com.flowerd.backend.entity.dto.inbound.SchemaVO;
import com.flowerd.backend.entity.dto.outbound.SchemaReturns;
import com.flowerd.backend.repository.ProjectRepository;
import com.flowerd.backend.repository.SchemaRepository;
import com.flowerd.backend.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SchemaService {

    private final SchemaRepository schemaRepository;
    private final TableService tableService;
    private final TableRepository tableRepository;
    private final ProjectRepository projectRepository;

    public Mono<ObjectId> saveSchema(SchemaVO schemaVO) {
        return projectRepository.findById(schemaVO.getProjectId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 프로젝트가 없습니다.")))
                .flatMap(project ->
                        schemaRepository.save(
                                new Schema(
                                        project.getId(),
                                        schemaVO.getSchemaName()
                                )
                        ).map(Schema::getId)
                );
    }


    public Mono<Void> deleteSchema(ObjectId schemaId) {
        return tableRepository.findAllBySchemaId(schemaId)
                .flatMap(table-> tableService.deleteTable(table.getId()))
                .then(schemaRepository.deleteById(schemaId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 스키마가 없습니다.")));
    }

    public Mono<Void> updateSchema(SchemaVO schemaVO, ObjectId schemaId) {
        return schemaRepository.findById(schemaId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당하는 스키마가 없습니다.")))
                .flatMap(schema -> {
                    if (schemaVO.getSchemaName() != null) {
                        schema.setSchemaName(schemaVO.getSchemaName());
                    }
                    return schemaRepository.save(schema);
                }).then();
    }

    public Mono<SchemaReturns> getListSchema(ObjectId schemaId) {
        return schemaRepository.findById(schemaId)
                .flatMap(schema -> {
                    SchemaReturns schemaReturns = new SchemaReturns();
                    schemaReturns.setSchemaId(schema.getId());
                    schemaReturns.setSchemaName(schema.getSchemaName());

                    // DiagramRepository에서 projectId로 연결된 다이어그램들을 조회하여 다이어그램 ID 목록 추출
                    return tableRepository.findAllBySchemaId(schemaId)
                            .flatMap(table -> tableService.getListTableByTableId(table.getId()))
                            .collectList()
                            .map(tableReturnsList -> {
                                schemaReturns.setTableReturns(tableReturnsList);
                                return schemaReturns;
                            });
                });
    }

}
