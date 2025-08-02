package com.railse.hiring.workforcemgmt.controller;

import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
@RequiredArgsConstructor
public class TaskManagementController {
    private final TaskManagementService taskManagementService;

    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }

    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody
                                                         TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }

    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody
                                                         UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }

    //Buggy endpoint 1
    @PostMapping("/assign-by-ref/v1")
    public Response<String> assignByReferenceV1(@RequestBody
                                                AssignByReferenceRequest request) {
        return new
                Response<>(taskManagementService.assignByReferenceV1(request));
    }

    //Fixed Bug 1
    @PostMapping("/assign-by-ref/v2")
    public Response<String> assignByReferenceV2(@RequestBody
                                                AssignByReferenceRequest request) {
        return new
                Response<>(taskManagementService.assignByReferenceV2(request));
    }

    @PostMapping("/fetch-by-date/v2")
    public Response<List<TaskManagementDto>> fetchByDateV2(@RequestBody
                                                           TaskFetchByDateRequest request) {
        return new
                Response<>(taskManagementService.fetchTasksByDate(request));
    }

    @PostMapping("/fetch-by-date/v3")
    public Response<List<TaskManagementDto>> fetchByDateV3(@RequestBody
                                                           TaskFetchByDateRequest request) {
        return new
                Response<>(taskManagementService.fetchTasksByDateV3(request));
    }


    //Endpoint for testing bug1
    @GetMapping("/reference/{referenceId}")
    public Response<List<TaskManagementDto>> getByReference(@PathVariable Long referenceId) {
        return new Response<>(taskManagementService.getByReference(referenceId));
    }


}
