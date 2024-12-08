package com.flowerd.backend.entity.enum_name;

import lombok.Getter;

@Getter
public enum ISKEY {
    PRIMARY_KEY("PK"),
    PRIMARY_KEY_AND_FOREIGN_KEY("PK/FK"),
    FOREIGN_KEY("FK"),
    NORMAL("NORMAL");

    private final String constraintType;

    ISKEY (String constraintType) {
        this.constraintType = constraintType;
    }
}
