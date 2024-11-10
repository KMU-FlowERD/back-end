package com.flowerd.backend.entity.enum_name;

import lombok.Getter;

@Getter
public enum DATATYPE {
    // 정수 타입 (Integer Types)
    TINYINT("TINYINT"),
    SMALLINT("SMALLINT"),
    MEDIUMINT("MEDIUMINT"),
    INT("INT"),
    INTEGER("INTEGER"),
    BIGINT("BIGINT"),
    BIT("BIT"),
    BOOLEAN("BOOLEAN"),  // BOOL과 동일
    BOOL("BOOL"),  // BOOLEAN과 동일

    // 고정 소수점 타입 (Fixed-Point Types)
    DECIMAL("DECIMAL"),
    DEC("DEC"),
    NUMERIC("NUMERIC"),

    // 부동 소수점 타입 (Floating-Point Types)
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    DOUBLE_PRECISION("DOUBLE PRECISION"),
    REAL("REAL"),

    // 날짜 및 시간 타입 (Date and Time Types)
    DATE("DATE"),
    DATETIME("DATETIME"),
    TIMESTAMP("TIMESTAMP"),
    TIME("TIME"),
    YEAR("YEAR"),

    // 문자열 타입 (String Types)
    CHAR("CHAR"),
    VARCHAR("VARCHAR"),
    BINARY("BINARY"),
    VARBINARY("VARBINARY"),
    TINYBLOB("TINYBLOB"),
    BLOB("BLOB"),
    MEDIUMBLOB("MEDIUMBLOB"),
    LONGBLOB("LONGBLOB"),
    TINYTEXT("TINYTEXT"),
    TEXT("TEXT"),
    MEDIUMTEXT("MEDIUMTEXT"),
    LONGTEXT("LONGTEXT"),
    ENUM("ENUM"),
    SET("SET"),


    // 기타 타입 (Other Types)
    JSON("JSON"),
    UUID("UUID");

    private final String type;

    DATATYPE(String type) {
        this.type = type;
    }
}
