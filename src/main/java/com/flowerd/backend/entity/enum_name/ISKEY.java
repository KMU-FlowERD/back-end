package com.flowerd.backend.entity.enum_name;

import lombok.Getter;

@Getter
public enum ISKEY {
    PRIMARY_KEY("PRIMARY KEY"),
    NORMAL("NORMAL");

    private final String constraintType;

    ISKEY (String constraintType) {
        this.constraintType = constraintType;
    }
}
