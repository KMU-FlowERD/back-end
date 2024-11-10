package com.flowerd.backend.service;

import com.flowerd.backend.entity.DiagramTable;
import com.flowerd.backend.entity.Table;
import com.flowerd.backend.entity.dto.inbound.DiagramTableVO;
import com.flowerd.backend.entity.dto.inbound.TableVO;
import com.flowerd.backend.entity.dto.outbound.ColumnReturns;
import com.flowerd.backend.entity.dto.outbound.ConstraintsReturns;
import com.flowerd.backend.entity.dto.outbound.TableDiagramReturns;
import com.flowerd.backend.entity.dto.outbound.TableReturns;
import com.flowerd.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final ColumnService columnService;
    private final ColumnRepository columnRepository;
    private final ConstraintsService constraintsService;
    private final ConstraintsRepository constraintsRepository;
    private final DiagramTableRepository diagramTableRepository;
    private final SchemaRepository schemaRepository;

    public Mono<ObjectId> saveTable(TableVO tableVO) {
        return schemaRepository.findById(tableVO.getSchemaId())
                .switchIfEmpty(Mono.error(new RuntimeException("스키마를 찾을 수 없습니다.")))
                .flatMap(schema ->
                        tableRepository.save(
                                new Table(
                                        tableVO.getSchemaId(),
                                        tableVO.getTableName()
                                )
                        )
                ).map(Table::getId);
    }




    public Mono<Void> deleteTable(ObjectId tableId) {
        return columnRepository.findAllByTableId(tableId)
                .switchIfEmpty(Mono.error(new RuntimeException("테이블이 존재하지 않습니다.")))
                .flatMap(column-> columnService.deleteColumn(column.getId()))
                .then(tableRepository.deleteById(tableId));
    }


    public Mono<Void> updateTable(TableVO tableVO, ObjectId tableId) {
        return tableRepository.findById(tableId)
                .switchIfEmpty(Mono.error(new RuntimeException("테이블이 존재하지 않습니다.")))
                .flatMap(table -> {
                    if (tableVO.getTableName() != null) {
                        table.setTableName(tableVO.getTableName());
                    }
                    return tableRepository.save(table)
                            .thenReturn(table); // Table을 저장하고 반환
                }).then();
    }




    // 테이블 - 스키마 구조
    public Mono<TableReturns> getListTableByTableId(ObjectId tableId) {
        return tableRepository.findById(tableId)
                .switchIfEmpty(Mono.error(new RuntimeException("테이블이 존재하지 않습니다.")))
                .flatMap(table -> {
                    TableReturns tableReturns = new TableReturns(table.getId(), table.getTableName(), null, null);

                    Mono<List<ColumnReturns>> columnReturnsList = columnRepository.findAllByTableId(tableId)
                            .flatMapSequential(column -> columnService.getListColumn(column.getId()))
                            .collectList();

                    Mono<List<ConstraintsReturns>> constraintsReturnsList = constraintsRepository.findAllByParentTableId(tableId)
                            .flatMapSequential(constraints -> constraintsService.getListConstraints(constraints.getId()))
                            .collectList();

                    return Mono.zip(columnReturnsList, constraintsReturnsList)
                            .map(tuple -> {
                                tableReturns.setColumns(tuple.getT1()); // Set columns
                                tableReturns.setConstraints(tuple.getT2()); // Set constraints
                                return tableReturns;
                            });

                });
    }

    // 테이블 - 다이어그램테이블 - 다이어그램 구조
    public Mono<TableDiagramReturns> getListTableByDiagramTableId(ObjectId diagramTableId){
        return diagramTableRepository.findById(diagramTableId)
                .switchIfEmpty(Mono.error(new RuntimeException("다이어그램 테이블이 존재하지 않습니다.")))
                .flatMap(diagramTable -> tableRepository.findById(diagramTable.getTableId())
                        .switchIfEmpty(Mono.error(new RuntimeException("테이블이 존재하지 않습니다.")))
                        .flatMap(table -> {
                            TableDiagramReturns tableDiagramReturns = new TableDiagramReturns(table.getId(), table.getTableName(), diagramTable.getSize_x(), diagramTable.getSize_y(), null, null);

                            Mono<List<ColumnReturns>> columnReturnsList = columnRepository.findAllByTableId(table.getId())
                                    .flatMap(column -> columnService.getListColumn(column.getId()))
                                    .collectList();

                            Mono<List<ConstraintsReturns>> constraintsReturnsList = constraintsRepository.findAllByParentTableId(table.getId())
                                    .flatMap(constraints -> constraintsService.getListConstraints(constraints.getId()))
                                    .collectList();

                            return Mono.zip(columnReturnsList, constraintsReturnsList)
                                    .map(tuple -> {
                                        tableDiagramReturns.setColumns(tuple.getT1()); // Set columns
                                        tableDiagramReturns.setConstraints(tuple.getT2()); // Set constraints
                                        return tableDiagramReturns;
                                    });
                        }));
    }

    public Mono<ObjectId> mapTableToDiagram(ObjectId tableId, ObjectId diagramId, DiagramTableVO diagramTableVO){
        return tableRepository.findById(tableId)
                .switchIfEmpty(Mono.error(new RuntimeException("테이블이 존재하지 않습니다.")))
                .flatMap(table -> diagramTableRepository.save(new DiagramTable(diagramId, tableId, diagramTableVO.getPosX(), diagramTableVO.getPosY()))
                        .map(DiagramTable::getId));
    }

    public Mono<Void> unmapTableToDiagram(ObjectId tableId, ObjectId diagramId){
        return diagramTableRepository.findByDiagramIdAndTableId(diagramId, tableId)
                .switchIfEmpty(Mono.error(new RuntimeException("다이어그램 테이블이 존재하지 않습니다.")))
                .flatMap(diagramTable -> diagramTableRepository.deleteById(diagramTable.getId()));
    }
}
