package com.railse.hiring.workforcemgmt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.workforcemgmt.model.enums.Priority;

//Dto which was not defined from codebase given in the pdf I am making use of records for efficiency
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public record UpdateTaskPriorityRequest(
        Long taskId,
        Priority newPriority
) {
}
