package com.flowerd.backend.service;

import com.flowerd.backend.entity.Column;
import com.flowerd.backend.entity.Constraints;
import com.flowerd.backend.entity.Table;
import com.flowerd.backend.entity.enum_name.DATATYPE;
import com.flowerd.backend.entity.enum_name.ISKEY;
import com.flowerd.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConvertService {
    private final SchemaRepository schemaRepository;
    private final TableRepository tableRepository;
    private final ColumnRepository columnRepository;
    private final ConstraintsRepository constraintsRepository;

    public Mono<String> generateDDL(ObjectId projectId) {
        return schemaRepository.findAllByProjectId(projectId)
                .flatMap(schema -> {
                    // Initialize DDL for each schema
                    StringBuilder ddl = new StringBuilder();
                    ddl.append("CREATE DATABASE IF NOT EXISTS `").append(schema.getSchemaName()).append("`;\n");
                    ddl.append("USE `").append(schema.getSchemaName()).append("`;\n\n");

                    // Process tables within the schema
                    return tableRepository.findAllBySchemaId(schema.getId())
                            .flatMap(this::generateTableDDL)
                            .collectList()
                            .map(tablesDDL -> {
                                tablesDDL.forEach(ddl::append);
                                return ddl.toString();
                            });
                })
                .collectList()
                .map(ddls -> String.join("\n", ddls));
    }

    private Mono<String> generateTableDDL(Table table) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE `").append(table.getTableName()).append("` (\n");

        return columnRepository.findAllByTableId(table.getId())
                .collectList()
                .flatMap(columns -> {
                    for (int i = 0; i < columns.size(); i++) {
                        Column column = columns.get(i);
                        ddl.append("  `").append(column.getColumnName()).append("` ")
                                .append(mapDataType(column.getDataType(), column.getLength()))
                                .append(column.getNullable() ? "" : " NOT NULL")
                                .append(column.getUnique() ? " UNIQUE" : "")
                                .append((column.getIsKey() == ISKEY.PRIMARY_KEY) ? " PRIMARY KEY" : "")
                                .append(i < columns.size() - 1 ? "," : "")
                                .append("\n");
                    }
                    ddl.append(");\n\n");

                    // Append constraints if available
                    return constraintsRepository.findAllByChildTableId(table.getId())
                            .flatMap(this::appendConstraint)
                            .collectList()
                            .map(constraintsDDL -> {
                                constraintsDDL.forEach(ddl::append);
                                return ddl.toString();
                            });
                });
    }

    private Mono<String> appendConstraint(Constraints constraint) {
        return Mono.zip(
                getTableNameById(constraint.getChildTableId()),
                getColumnNameById(constraint.getChildColumnId()),
                getTableNameById(constraint.getParentTableId()),
                getColumnNameById(constraint.getParentColumnId())
        ).map(tuple -> {
            String childTableName = tuple.getT1();
            String childColumnName = tuple.getT2();
            String parentTableName = tuple.getT3();
            String parentColumnName = tuple.getT4();

            return "ALTER TABLE `" + childTableName + "`\n" +
                    "  ADD CONSTRAINT `fk_" + constraint.getChildTableId() + "_" + constraint.getParentTableId() + "`\n" +
                    "  FOREIGN KEY (`" + childColumnName + "`)\n" +
                    "  REFERENCES `" + parentTableName + "` (`" + parentColumnName + "`);\n\n";
        });
    }

    private String mapDataType(DATATYPE dataType, Integer length) {
        return switch (dataType) {
            case VARCHAR -> "VARCHAR(" + (length != null ? length : 255) + ")";
            case INT -> "INT";
            case BOOLEAN -> "BOOLEAN";
            default -> "TEXT";
        };
    }

    private Mono<String> getTableNameById(ObjectId tableId) {
        return tableRepository.findById(tableId).map(Table::getTableName);
    }

    private Mono<String> getColumnNameById(ObjectId columnId) {
        return columnRepository.findById(columnId).map(Column::getColumnName);
    }
}
