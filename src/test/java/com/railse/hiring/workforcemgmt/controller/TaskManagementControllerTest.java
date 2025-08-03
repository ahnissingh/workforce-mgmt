package com.railse.hiring.workforcemgmt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskManagementController.class)
class TaskManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskManagementService taskManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskManagementDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = new TaskManagementDto();
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        when(taskManagementService.findTaskById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/task-mgmt/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldCreateTasks() throws Exception {
        TaskCreateRequest.RequestItem item = new TaskCreateRequest.RequestItem();
        item.setReferenceId(1L);
        item.setReferenceType(ReferenceType.ORDER);
        item.setTask(Task.CREATE_INVOICE);
        item.setAssigneeId(1L);
        item.setPriority(Priority.HIGH);
        item.setTaskDeadlineTime(System.currentTimeMillis());

        TaskCreateRequest request = new TaskCreateRequest();
        request.setRequests(List.of(item));
        when(taskManagementService.createTasks(any())).thenReturn(List.of(sampleDto));

        mockMvc.perform(post("/task-mgmt/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldUpdateTasks() throws Exception {
        UpdateTaskRequest.RequestItem item = new UpdateTaskRequest.RequestItem();
        item.setTaskId(1L);
        item.setTaskStatus(TaskStatus.STARTED);
        item.setDescription("Started");

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setRequests(List.of(item));
        when(taskManagementService.updateTasks(any())).thenReturn(List.of(sampleDto));

        mockMvc.perform(post("/task-mgmt/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldAssignByReferenceV2() throws Exception {
        AssignByReferenceRequest req = new AssignByReferenceRequest();
        req.setReferenceId(201L);
        req.setReferenceType(ReferenceType.ENTITY);
        req.setAssigneeId(5L);

        when(taskManagementService.assignByReferenceV2(any())).thenReturn("done");

        mockMvc.perform(post("/task-mgmt/assign-by-ref/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("done"));
    }

    @Test
    void shouldFetchByPriority() throws Exception {
        when(taskManagementService.getTasksByPriority(Priority.HIGH)).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/task-mgmt/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
    @Test
    void shouldFetchByDateV2() throws Exception {
        TaskFetchByDateRequest req = new TaskFetchByDateRequest(0L, System.currentTimeMillis(), List.of(1L));
        when(taskManagementService.fetchTasksByDateV2(any())).thenReturn(List.of(new TaskManagementDto()));

        mockMvc.perform(post("/task-mgmt/fetch-by-date/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }


    @Test
    void shouldFetchByDateV3() throws Exception {
        TaskFetchByDateRequest req = new TaskFetchByDateRequest(0L, System.currentTimeMillis(), List.of(1L));
        when(taskManagementService.fetchTasksByDateV3(any())).thenReturn(List.of(new TaskManagementDto()));

        mockMvc.perform(post("/task-mgmt/fetch-by-date/v3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldFetchByDateV4() throws Exception {
        TaskFetchByDateRequest req = new TaskFetchByDateRequest(0L, System.currentTimeMillis(), List.of(1L));
        when(taskManagementService.fetchTasksByDateV4(any())).thenReturn(List.of(new TaskManagementDto()));

        mockMvc.perform(post("/task-mgmt/fetch-by-date/v4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldUpdatePriority() throws Exception {
        UpdateTaskPriorityRequest req = new UpdateTaskPriorityRequest(1L, Priority.HIGH);
        when(taskManagementService.updateTaskPriority(any())).thenReturn(new TaskManagementDto());

        mockMvc.perform(post("/task-mgmt/priority/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldGetByPriority() throws Exception {
        when(taskManagementService.getTasksByPriority(Priority.HIGH)).thenReturn(List.of(new TaskManagementDto()));

        mockMvc.perform(get("/task-mgmt/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldGetTaskDetails() throws Exception {
        when(taskManagementService.getTaskDetails(1L)).thenReturn(new TaskManagementDto());

        mockMvc.perform(get("/task-mgmt/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldGetByReference() throws Exception {
        when(taskManagementService.getByReference(101L)).thenReturn(List.of(new TaskManagementDto()));

        mockMvc.perform(get("/task-mgmt/reference/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
    @Test
    void shouldAddCommentToTask() throws Exception {
        AddCommentRequest req = new AddCommentRequest(1L, "Sample comment");
        when(taskManagementService.addCommentToTask(any())).thenReturn(new TaskManagementDto());

        mockMvc.perform(post("/task-mgmt/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }


}
