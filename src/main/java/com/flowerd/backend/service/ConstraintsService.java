package com.flowerd.backend.service;

import com.flowerd.backend.entity.Constraints;
import com.flowerd.backend.entity.dto.inbound.ConstraintsVO;
import com.flowerd.backend.entity.enum_name.CARDINALITY;
import com.flowerd.backend.entity.enum_name.PARTICIPATION;
import com.flowerd.backend.entity.enum_name.RELTYPE;
import com.flowerd.backend.entity.dto.outbound.ConstraintsReturns;
import com.flowerd.backend.repository.ColumnRepository;
import com.flowerd.backend.repository.ConstraintsRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConstraintsService {

    private final ConstraintsRepository constraintsRepository;
    private final ColumnRepository columnRepository;

    public Mono<ConstraintsReturns> getListConstraints(ObjectId constraintsId) {
        return constraintsRepository.findById(constraintsId)
                .switchIfEmpty(Mono.error(new RuntimeException("칼럼이 존재하지 않습니다.")))
                .map(constraints -> new ConstraintsReturns(constraints.getId(), constraints.getChildTableId(), constraints.getChildColumnId(),constraints.getParentTableId(), constraints.getParentColumnId(), constraints.getParentParticipation(), constraints.getChildParticipation(), constraints.getParentCardinality(), constraints.getChildCardinality(), constraints.getRelType()));
    }

    public Mono<ObjectId> saveConstraints(ConstraintsVO constraintsVO) {
        return columnRepository.findById(constraintsVO.getChildColumnId())
                .switchIfEmpty(Mono.error(new RuntimeException("칼럼이 존재하지 않습니다.")))
                .zipWith(columnRepository.findById(constraintsVO.getParentColumnId()))
                .switchIfEmpty(Mono.error(new RuntimeException("칼럼이 존재하지 않습니다.")))
                .flatMap(tuple ->
                        constraintsRepository.save(
                                new Constraints(
                                        constraintsVO.getChildTableId(),
                                        constraintsVO.getChildColumnId(),
                                        constraintsVO.getParentTableId(),
                                        constraintsVO.getParentColumnId(),
                                        constraintsVO.getParentParticipation(),
                                        constraintsVO.getChildParticipation(),
                                        constraintsVO.getParentCardinality(),
                                        constraintsVO.getChildCardinality(),
                                        constraintsVO.getRelType()))
                        .map(Constraints::getId)
                );
    }


    public Mono<Void> deleteConstraints(ObjectId constraintsId) {
        return constraintsRepository.deleteById(constraintsId)
                .switchIfEmpty(Mono.error(new RuntimeException("제약조건이 존재하지 않습니다.")));
    }

    public Mono<Void> updateConstraints(ConstraintsVO constraintsVO, ObjectId constraintsId) {
        return constraintsRepository.findById(constraintsId)
                .switchIfEmpty(Mono.error(new RuntimeException("제약조건이 존재하지 않습니다.")))
                .flatMap(constraints -> {
                    Mono<Boolean> childColumnExists = constraintsVO.getChildColumnId() == null
                            ? Mono.just(true)
                            : columnRepository.findById(constraintsVO.getChildColumnId()).hasElement();

                    Mono<Boolean> parentColumnExists = constraintsVO.getParentColumnId() == null
                            ? Mono.just(true)
                            : columnRepository.findById(constraintsVO.getParentColumnId()).hasElement();

                    return Mono.zip(childColumnExists, parentColumnExists)
                            .flatMap(tuple -> {
                                boolean childExists = tuple.getT1();
                                boolean parentExists = tuple.getT2();

                                if (!childExists || !parentExists) {
                                    return Mono.error(new RuntimeException("칼럼이 존재하지 않습니다."));
                                }

                                // 업데이트할 필드가 존재하면 설정
                                if (constraintsVO.getChildTableId() != null) {
                                    constraints.setChildTableId(constraintsVO.getChildTableId());
                                }
                                if (constraintsVO.getChildColumnId() != null) {
                                    constraints.setChildColumnId(constraintsVO.getChildColumnId());
                                }
                                if (constraintsVO.getParentTableId() != null) {
                                    constraints.setParentTableId(constraintsVO.getParentTableId());
                                }
                                if (constraintsVO.getParentColumnId() != null) {
                                    constraints.setParentColumnId(constraintsVO.getParentColumnId());
                                }
                                if (constraintsVO.getParentParticipation() != null) {
                                    constraints.setParentParticipation(constraintsVO.getParentParticipation());
                                }
                                if (constraintsVO.getChildParticipation() != null) {
                                    constraints.setChildParticipation(constraintsVO.getChildParticipation());
                                }
                                if (constraintsVO.getParentCardinality() != null) {
                                    constraints.setParentCardinality(constraintsVO.getParentCardinality());
                                }
                                if (constraintsVO.getChildCardinality() != null) {
                                    constraints.setChildCardinality(constraintsVO.getChildCardinality());
                                }
                                if (constraintsVO.getRelType() != null) {
                                    constraints.setRelType(constraintsVO.getRelType());
                                }

                                return constraintsRepository.save(constraints);
                            });
                })
                .then();
    }

}
