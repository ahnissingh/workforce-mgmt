package com.railse.hiring.workforcemgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddCommentRequest(
        @JsonProperty("task_id")
        Long taskId,
        @JsonProperty("message")
        String message
) {
}
