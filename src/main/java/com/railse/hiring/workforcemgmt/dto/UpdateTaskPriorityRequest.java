package com.railse.hiring.workforcemgmt.dto;

import com.railse.hiring.workforcemgmt.model.enums.Priority;

//Dto which was not defined from codebase given in the pdf I am making use of records for efficiency
public record UpdateTaskPriorityRequest(
        Long taskId,
        Priority newPriority
) {
}
