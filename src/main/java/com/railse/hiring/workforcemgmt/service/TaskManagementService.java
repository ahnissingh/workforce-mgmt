package com.railse.hiring.workforcemgmt.service;

import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.enums.Priority;

import java.util.List;

public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);

    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);

    String assignByReferenceV1(AssignByReferenceRequest request);

    //                TaskManagement newTask = new TaskManagement();
//                newTask.setReferenceId(request.getReferenceId());
//                newTask.setReferenceType(request.getReferenceType());
//                newTask.setTask(taskType);
//                newTask.setAssigneeId(request.getAssigneeId());
//                newTask.setStatus(TaskStatus.ASSIGNED);
//                taskRepository.save(newTask);
//            }
//        }
//        return "Tasks assigned successfully for reference " +
//                request.getReferenceId();
//    }
    String assignByReferenceV2(AssignByReferenceRequest request);

    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest
                                                     request);

    TaskManagementDto findTaskById(Long id);

    //Fixed bug 2
    List<TaskManagementDto> fetchTasksByDateV2(TaskFetchByDateRequest
                                                       request);


    List<TaskManagementDto> getByReference(Long referenceId);

    List<TaskManagementDto> fetchTasksByDateV3(TaskFetchByDateRequest request);


    List<TaskManagementDto> fetchTasksByDateV4(TaskFetchByDateRequest request);

    TaskManagementDto updateTaskPriority(UpdateTaskPriorityRequest request);
    List<TaskManagementDto> getTasksByPriority(Priority priority);
}
