package com.railse.hiring.workforcemgmt.dto;

public record AddCommentRequest(
        Long taskId,
        String message
){}
