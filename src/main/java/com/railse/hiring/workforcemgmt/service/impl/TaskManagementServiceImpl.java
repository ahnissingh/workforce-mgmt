package com.railse.hiring.workforcemgmt.service.impl;

import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {
    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;

    public TaskManagementServiceImpl(TaskRepository taskRepository,
                                     ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id:" + id));
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest
                                                       createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item :
                createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            createdTasks.add(taskRepository.save(newTask));
        }
        return taskMapper.modelListToDtoList(createdTasks);
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest
                                                       updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item :
                updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id:" + item.getTaskId()));
            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }

    //    Bug1 code
    @Override
    public String assignByReferenceV1(AssignByReferenceRequest request) {
        List<Task> applicableTasks =
                Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks =
                taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(),
                        request.getReferenceType());
        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus()
                            != TaskStatus.COMPLETED)
                    .toList();
// BUG #1 is here. It should assign one and cancel the rest.
// Instead, it reassigns ALL of them.
            if (!tasksOfType.isEmpty()) {
                for (TaskManagement taskToUpdate : tasksOfType) {
                    taskToUpdate.setAssigneeId(request.getAssigneeId());
                    taskRepository.save(taskToUpdate);
                }
            } else {

                //Create a new task if none exist
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                taskRepository.save(newTask);
            }
        }
        return "Tasks assigned successfully for reference " +
                request.getReferenceId();
    }

    @Override
    public String assignByReferenceV2(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());

        List<TaskManagement> existingTasks =
                taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(),
                        request.getReferenceType());

        for (Task taskType : applicableTasks) {
            // Cancelling all existing tasks of this type that are not already completed/cancelled
            for (TaskManagement existing : existingTasks) {
                if (existing.getTask() == taskType &&
                        existing.getStatus() != TaskStatus.COMPLETED &&
                        existing.getStatus() != TaskStatus.CANCELLED) {

                    existing.setStatus(TaskStatus.CANCELLED);
                    existing.setDescription("Cancelled due to reassignment");
                    taskRepository.save(existing);
                }
            }

            //creating  a fresh task for the new assignee
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(request.getReferenceId());
            newTask.setReferenceType(request.getReferenceType());
            newTask.setTask(taskType);
            newTask.setAssigneeId(request.getAssigneeId());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setPriority(Priority.MEDIUM); // Default, or add to request if needed
            newTask.setDescription("Newly assigned via assign-by-ref");
            newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000); // 1 day

            taskRepository.save(newTask);
        }

        return "Tasks reassigned successfully for reference " + request.getReferenceId();
    }

    //Bug2 code
    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest
                                                            request) {
        List<TaskManagement> tasks =
                taskRepository.findByAssigneeIdIn(request.getAssigneeIds());
        // BUG #2 is here. It should filter out CANCELLED tasks butdoesn't.
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> {

                    // This logic is incomplete for the assignment.
                    // It should check against startDate and endDate.
                    // For now, it just returns all tasks for the assignees.
                    return true;
                })
                .collect(Collectors.toList());
        return taskMapper.modelListToDtoList(filteredTasks);
    }

    //Fixed bug 2
    @Override
    public List<TaskManagementDto> fetchTasksByDateV2(TaskFetchByDateRequest
                                                              request) {
        List<TaskManagement> tasks =
                taskRepository.findByAssigneeIdIn(request.getAssigneeIds());
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED
                        && task.getTaskDeadlineTime() >= request.getStartDate()
                        && task.getTaskDeadlineTime() <= request.getEndDate())
                .toList();//this is better to return as it is unmodifiable , we are just mapping and returning the list
        return taskMapper.modelListToDtoList(filteredTasks);
    }

    //New Method just for showing bug 1
    @Override
    public List<TaskManagementDto> getByReference(Long referenceId) {
        List<TaskManagement> all = taskRepository.findAll();
        List<TaskManagement> filtered = all.stream()
                .filter(t -> t.getReferenceId().equals(referenceId))
                .collect(Collectors.toList());
        return taskMapper.modelListToDtoList(filtered);
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDateV3(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED) //  fix  is here
                .filter(task -> task.getTaskDeadlineTime() >= request.getStartDate()
                        && task.getTaskDeadlineTime() <= request.getEndDate())
                .map(taskMapper::modelToDto)
                .collect(Collectors.toList());
    }


}
