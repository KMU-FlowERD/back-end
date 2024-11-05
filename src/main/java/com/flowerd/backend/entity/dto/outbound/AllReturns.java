package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllReturns {
    List<ProjectReturns> projectReturns;
    List<ProjectDrawReturns> projectDrawReturns;
}
