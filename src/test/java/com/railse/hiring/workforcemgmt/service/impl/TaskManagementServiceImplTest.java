package com.railse.hiring.workforcemgmt.service.impl;

import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.impl.TaskManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskManagementServiceImplTest {

    private TaskRepository taskRepository;
    private ITaskManagementMapper taskMapper;
    private TaskManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskMapper = mock(ITaskManagementMapper.class);
        service = new TaskManagementServiceImpl(taskRepository, taskMapper);
    }

    @Test
    void findTaskById() {
        TaskManagement task = new TaskManagement();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.modelToDto(task)).thenReturn(new TaskManagementDto());

        TaskManagementDto result = service.findTaskById(1L);
        assertNotNull(result);
    }

    @Test
    void createTasks() {
        TaskCreateRequest.RequestItem item = new TaskCreateRequest.RequestItem();
        item.setReferenceId(1L);
        item.setReferenceType(ReferenceType.ORDER);
        item.setTask(Task.CREATE_INVOICE);
        item.setAssigneeId(1L);
        item.setPriority(Priority.HIGH);
        item.setTaskDeadlineTime(System.currentTimeMillis());

        TaskCreateRequest req = new TaskCreateRequest();
        req.setRequests(List.of(item));

        TaskManagement saved = new TaskManagement();
        when(taskRepository.save(any())).thenReturn(saved);
        when(taskMapper.modelListToDtoList(any())).thenReturn(List.of(new TaskManagementDto()));

        List<TaskManagementDto> result = service.createTasks(req);
        assertEquals(1, result.size());
    }

    @Test
    void updateTasks() {
        UpdateTaskRequest.RequestItem item = new UpdateTaskRequest.RequestItem();
        item.setDescription("Updated description");
        item.setTaskStatus(TaskStatus.COMPLETED);
        item.setTaskId(1L);

        UpdateTaskRequest req = new UpdateTaskRequest();
        req.setRequests(List.of(item));

        TaskManagement task = new TaskManagement();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.modelListToDtoList(any())).thenReturn(List.of(new TaskManagementDto()));

        List<TaskManagementDto> result = service.updateTasks(req);
        assertEquals(1, result.size());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void assignByReferenceV1() {
        AssignByReferenceRequest req = new AssignByReferenceRequest();
        req.setReferenceId(201L);
        req.setReferenceType(ReferenceType.ENTITY);
        req.setAssigneeId(1L);

        Task taskType = Task.ASSIGN_CUSTOMER_TO_SALES_PERSON;
        TaskManagement existing = new TaskManagement();
        existing.setTask(taskType);
        existing.setStatus(TaskStatus.ASSIGNED);

        when(taskRepository.findByReferenceIdAndReferenceType(201L, ReferenceType.ENTITY))
                .thenReturn(List.of(existing));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = service.assignByReferenceV1(req);
        assertEquals("Tasks assigned successfully for reference 201", result);
    }

    @Test
    void assignByReferenceV2() {
        AssignByReferenceRequest req = new AssignByReferenceRequest();
        req.setReferenceId(201L);
        req.setReferenceType(ReferenceType.ENTITY);
        req.setAssigneeId(1L);

        Task taskType = Task.ASSIGN_CUSTOMER_TO_SALES_PERSON;
        TaskManagement existing = new TaskManagement();
        existing.setTask(taskType);
        existing.setStatus(TaskStatus.ASSIGNED);

        when(taskRepository.findByReferenceIdAndReferenceType(201L, ReferenceType.ENTITY))
                .thenReturn(List.of(existing));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = service.assignByReferenceV2(req);
        assertTrue(result.contains("Tasks reassigned successfully"));
    }

    @Test
    void fetchTasksByDate() {
        TaskManagement task = new TaskManagement();
        task.setStatus(TaskStatus.CANCELLED);

        when(taskRepository.findByAssigneeIdIn(any())).thenReturn(List.of(task));
        when(taskMapper.modelListToDtoList(any())).thenReturn(List.of(new TaskManagementDto()));

        TaskFetchByDateRequest req = new TaskFetchByDateRequest(0L, System.currentTimeMillis(), List.of(1L));
        List<TaskManagementDto> result = service.fetchTasksByDate(req);

        assertEquals(1, result.size()); // Because buggy version doesn't filter cancelled
    }

    @Test
    void fetchTasksByDateV2() {
        TaskManagement active = new TaskManagement();
        active.setStatus(TaskStatus.ASSIGNED);
        active.setTaskDeadlineTime(System.currentTimeMillis());

        when(taskRepository.findByAssigneeIdIn(any())).thenReturn(List.of(active));
        when(taskMapper.modelListToDtoList(any())).thenReturn(List.of(new TaskManagementDto()));

        TaskFetchByDateRequest req = new TaskFetchByDateRequest(0L, System.currentTimeMillis(), List.of(1L));
        List<TaskManagementDto> result = service.fetchTasksByDateV2(req);

        assertEquals(1, result.size());
    }

    @Test
    void getByReference() {
        TaskManagement t = new TaskManagement();
        t.setReferenceId(201L);

        when(taskRepository.findAll()).thenReturn(List.of(t));
        when(taskMapper.modelListToDtoList(any())).thenReturn(List.of(new TaskManagementDto()));

        List<TaskManagementDto> result = service.getByReference(201L);
        assertEquals(1, result.size());
    }

    @Test
    void fetchTasksByDateV3() {
        TaskManagement t = new TaskManagement();
        t.setStatus(TaskStatus.ASSIGNED);
        t.setTaskDeadlineTime(System.currentTimeMillis());

        when(taskRepository.findByAssigneeIdIn(any())).thenReturn(List.of(t));
        when(taskMapper.modelToDto(any())).thenReturn(new TaskManagementDto());

        TaskFetchByDateRequest req = new TaskFetchByDateRequest(0L, System.currentTimeMillis(), List.of(1L));
        List<TaskManagementDto> result = service.fetchTasksByDateV3(req);
        assertEquals(1, result.size());
    }

    @Test
    void fetchTasksByDateV4() {
        TaskManagement t1 = new TaskManagement();
        t1.setTaskDeadlineTime(System.currentTimeMillis());
        t1.setStatus(TaskStatus.STARTED);

        TaskManagement t2 = new TaskManagement();
        t2.setTaskDeadlineTime(System.currentTimeMillis() - 86400000);
        t2.setStatus(TaskStatus.ASSIGNED);

        when(taskRepository.findByAssigneeIdIn(any())).thenReturn(List.of(t1, t2));
        when(taskMapper.modelToDto(any())).thenReturn(new TaskManagementDto());

        TaskFetchByDateRequest req = new TaskFetchByDateRequest(System.currentTimeMillis() - 1000, System.currentTimeMillis() + 1000, List.of(1L));
        List<TaskManagementDto> result = service.fetchTasksByDateV4(req);
        assertEquals(2, result.size());
    }

    @Test
    void updateTaskPriority() {
        TaskManagement task = new TaskManagement();
        task.setActivityHistory(new ArrayList<>());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.modelToDto(task)).thenReturn(new TaskManagementDto());

        TaskManagementDto result = service.updateTaskPriority(new UpdateTaskPriorityRequest(1L, Priority.HIGH));
        assertNotNull(result);
    }

    @Test
    void getTasksByPriority() {
        TaskManagement t = new TaskManagement();
        t.setPriority(Priority.HIGH);

        when(taskRepository.findAll()).thenReturn(List.of(t));
        when(taskMapper.modelToDto(t)).thenReturn(new TaskManagementDto());

        List<TaskManagementDto> result = service.getTasksByPriority(Priority.HIGH);
        assertEquals(1, result.size());
    }

    @Test
    void addCommentToTask() {
        TaskManagement t = new TaskManagement();
        t.setComments(new ArrayList<>());
        t.setActivityHistory(new ArrayList<>());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(t));
        when(taskRepository.save(t)).thenReturn(t);
        when(taskMapper.modelToDto(t)).thenReturn(new TaskManagementDto());

        TaskManagementDto result = service.addCommentToTask(new AddCommentRequest(1L, "note"));
        assertNotNull(result);
    }

    @Test
    void getTaskDetails() {
        TaskManagement t = new TaskManagement();
        t.setActivityHistory(new ArrayList<>());
        t.setComments(new ArrayList<>());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(t));
        when(taskMapper.modelToDto(t)).thenReturn(new TaskManagementDto());

        TaskManagementDto result = service.getTaskDetails(1L);
        assertNotNull(result);
    }
}
