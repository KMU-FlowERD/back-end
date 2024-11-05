package com.flowerd.backend.entity.enum_name;

import lombok.Getter;

@Getter
public enum PARTICIPATION {
    FULL("FULL"),
    PARTIAL("PARTIAL");

    private final String participation;
    PARTICIPATION(String participation) {
        this.participation = participation;
    }
}
