package com.flowerd.backend.entity.enum_name;

import lombok.Getter;

@Getter
public enum RELTYPE {
    IDENTIFYING("IDENTIFYING"),
    NON_IDENTIFYING("NON_IDENTIFYING");

    private final String relType;

    RELTYPE(String relType) {
        this.relType = relType;
    }
}
