package com.flowerd.backend.service;

import com.flowerd.backend.entity.Column;
import com.flowerd.backend.entity.dto.inbound.ColumnVO;
import com.flowerd.backend.entity.enum_name.DATATYPE;
import com.flowerd.backend.entity.enum_name.ISKEY;
import com.flowerd.backend.entity.dto.outbound.ColumnReturns;
import com.flowerd.backend.repository.ColumnRepository;
import com.flowerd.backend.repository.ConstraintsRepository;
import com.flowerd.backend.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ConstraintsRepository constraintsRepository;
    private final TableRepository tableRepository;

    public Mono<Void> deleteColumn(ObjectId columnId) {
        return constraintsRepository.deleteAllByParentColumnId(columnId)
                .then(constraintsRepository.deleteAllByChildColumnId(columnId))
                .then(columnRepository.deleteById(columnId))
                .switchIfEmpty(Mono.error(new RuntimeException("컬럼이 존재하지 않습니다.")));
    }


    public Mono<Void> updateColumn(ColumnVO columnVO, ObjectId columnId) {
        return columnRepository.findById(columnId)
                .switchIfEmpty(Mono.error(new RuntimeException("컬럼이 존재하지 않습니다.")))
                .flatMap(column -> {
                    if (columnVO.getColumnName() != null) {
                        column.setColumnName(columnVO.getColumnName());
                    }
                    if (columnVO.getNullable() != null) {
                        column.setNullable(columnVO.getNullable());
                    }
                    if (columnVO.getUnique() != null) {
                        column.setUnique(columnVO.getUnique());
                    }
                    if (columnVO.getIsKey() != null) {
                        column.setIsKey(columnVO.getIsKey());
                    }
                    if (columnVO.getDataType() != null) {
                        column.setDataType(columnVO.getDataType());
                    }
                    if (columnVO.getLength() != null) {
                        column.setLength(columnVO.getLength());
                    }
                    return columnRepository.save(column);
                }).then();
    }


    public Mono<ColumnReturns> getListColumn(ObjectId columnId) {
        return columnRepository.findById(columnId)
                .switchIfEmpty(Mono.error(new RuntimeException("컬럼이 존재하지 않습니다.")))
                .map(column -> new ColumnReturns(column.getId(), column.getColumnName(), column.getNullable(), column.getUnique(), column.getIsKey(), column.getDataType(), column.getLength()));
    }

    public Mono<ObjectId> saveColumn(ColumnVO columnVO) {
        return tableRepository.findById(columnVO.getTableId())
                .switchIfEmpty(Mono.error(new RuntimeException("테이블이 존재하지 않습니다.")))
                .flatMap(table ->
                        columnRepository.save(
                                new Column(
                                        columnVO.getTableId(),
                                        columnVO.getColumnName(),
                                        columnVO.getNullable(),
                                        columnVO.getUnique(),
                                        columnVO.getIsKey(),
                                        columnVO.getDataType(),
                                        columnVO.getLength()
                                )
                        ).map(Column::getId)
                );
    }

}
