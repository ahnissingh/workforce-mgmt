package com.railse.hiring.workforcemgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.railse.hiring.workforcemgmt.model.enums.Priority;

//Dto which was not defined from codebase given in the pdf I am making use of records for efficiency
public record UpdateTaskPriorityRequest(
        @JsonProperty("task_id") Long taskId,
        @JsonProperty("new_priority") Priority newPriority
) {}

