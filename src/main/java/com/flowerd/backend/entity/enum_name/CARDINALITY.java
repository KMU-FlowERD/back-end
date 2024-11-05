package com.flowerd.backend.entity.enum_name;

import lombok.Getter;

@Getter
public enum CARDINALITY {
    ONE("ONE"),
    MANY("MANY");

    private final String cardinality;

    CARDINALITY(String cardinality) {
        this.cardinality = cardinality;
    }
}
