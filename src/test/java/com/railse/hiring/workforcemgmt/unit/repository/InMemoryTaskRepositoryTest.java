package com.railse.hiring.workforcemgmt.unit.repository;

import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskRepository Unit Tests")
class InMemoryTaskRepositoryTest {

    private InMemoryTaskRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
    }

    @Test
    @DisplayName("Should have seed data loaded on initialization")
    void shouldHaveSeedDataLoadedOnInitialization() {
        // When
        List<TaskManagement> allTasks = repository.findAll();

        // Then
        assertEquals(6, allTasks.size()); // 6 seed tasks are created in constructor

        // Verify seed data properties
        Optional<TaskManagement> firstTask = repository.findById(1L);
        assertTrue(firstTask.isPresent());
        assertEquals(101L, firstTask.get().getReferenceId());
        assertEquals(ReferenceType.ORDER, firstTask.get().getReferenceType());
        assertEquals(Task.CREATE_INVOICE, firstTask.get().getTask());
        assertEquals(1L, firstTask.get().getAssigneeId());
        assertEquals(TaskStatus.ASSIGNED, firstTask.get().getStatus());
        assertEquals(Priority.HIGH, firstTask.get().getPriority());
        assertEquals("This is a seed task.", firstTask.get().getDescription());
    }

    @Test
    @DisplayName("Should save new task successfully")
    void shouldSaveNewTaskSuccessfully() {
        // Given
        TaskManagement newTask = TaskManagement.builder()
                .referenceId(999L)
                .referenceType(ReferenceType.ORDER)
                .task(Task.CREATE_INVOICE)
                .description("New test task")
                .assigneeId(100L)
                .status(TaskStatus.ASSIGNED)
                .priority(Priority.MEDIUM)
                .taskDeadlineTime(System.currentTimeMillis() + 86400000)
                .build();

        // When
        TaskManagement savedTask = repository.save(newTask);

        // Then
        assertNotNull(savedTask.getId());
        assertEquals(999L, savedTask.getReferenceId());
        assertEquals(ReferenceType.ORDER, savedTask.getReferenceType());
        assertEquals(Task.CREATE_INVOICE, savedTask.getTask());
        assertEquals("New test task", savedTask.getDescription());
        assertEquals(100L, savedTask.getAssigneeId());
        assertEquals(TaskStatus.ASSIGNED, savedTask.getStatus());
        assertEquals(Priority.MEDIUM, savedTask.getPriority());
    }

    @Test
    @DisplayName("Should find task by id when exists")
    void shouldFindTaskByIdWhenExists() {
        // When
        Optional<TaskManagement> foundTask = repository.findById(1L);

        // Then
        assertTrue(foundTask.isPresent());
        assertEquals(1L, foundTask.get().getId());
        assertEquals(101L, foundTask.get().getReferenceId());
    }

    @Test
    @DisplayName("Should return empty when task not found")
    void shouldReturnEmptyWhenTaskNotFound() {
        // When
        Optional<TaskManagement> foundTask = repository.findById(999L);

        // Then
        assertFalse(foundTask.isPresent());
    }

    @Test
    @DisplayName("Should find all tasks including seed data")
    void shouldFindAllTasksIncludingSeedData() {
        // When
        List<TaskManagement> allTasks = repository.findAll();

        // Then
        assertEquals(6, allTasks.size()); // 6 seed tasks

        // Verify all tasks have IDs
        assertTrue(allTasks.stream().allMatch(task -> task.getId() != null));

        // Verify seed data is present
        assertTrue(allTasks.stream().anyMatch(task -> task.getReferenceId().equals(101L)));
        assertTrue(allTasks.stream().anyMatch(task -> task.getReferenceId().equals(102L)));
        assertTrue(allTasks.stream().anyMatch(task -> task.getReferenceId().equals(201L)));
    }

    @Test
    @DisplayName("Should find tasks by reference id and type")
    void shouldFindTasksByReferenceIdAndType() {
        // When
        List<TaskManagement> orderTasks = repository.findByReferenceIdAndReferenceType(101L, ReferenceType.ORDER);
        List<TaskManagement> entityTasks = repository.findByReferenceIdAndReferenceType(201L, ReferenceType.ENTITY);

        // Then
        assertEquals(2, orderTasks.size()); // 2 tasks with referenceId 101 and type ORDER
        assertEquals(2, entityTasks.size()); // 2 tasks with referenceId 201 and type ENTITY

        // Verify all returned tasks match the criteria
        assertTrue(orderTasks.stream().allMatch(task ->
                task.getReferenceId().equals(101L) && task.getReferenceType().equals(ReferenceType.ORDER)));
        assertTrue(entityTasks.stream().allMatch(task ->
                task.getReferenceId().equals(201L) && task.getReferenceType().equals(ReferenceType.ENTITY)));
    }

    @Test
    @DisplayName("Should find tasks by assignee ids")
    void shouldFindTasksByAssigneeIds() {
        // When
        List<TaskManagement> assignee1Tasks = repository.findByAssigneeIdIn(List.of(1L));
        List<TaskManagement> assignee2Tasks = repository.findByAssigneeIdIn(List.of(2L));
        List<TaskManagement> multipleAssigneeTasks = repository.findByAssigneeIdIn(List.of(1L, 2L));

        // Then
        assertEquals(3, assignee1Tasks.size()); // 3 tasks assigned to assignee 1
        assertEquals(2, assignee2Tasks.size()); // 2 tasks assigned to assignee 2
        assertEquals(5, multipleAssigneeTasks.size()); // 5 tasks assigned to either assignee 1 or 2

        // Verify all returned tasks match the criteria
        assertTrue(assignee1Tasks.stream().allMatch(task -> task.getAssigneeId().equals(1L)));
        assertTrue(assignee2Tasks.stream().allMatch(task -> task.getAssigneeId().equals(2L)));
        assertTrue(multipleAssigneeTasks.stream().allMatch(task ->
                task.getAssigneeId().equals(1L) || task.getAssigneeId().equals(2L)));
    }

    @Test
    @DisplayName("Should update existing task")
    void shouldUpdateExistingTask() {
        // Given
        TaskManagement existingTask = repository.findById(1L).orElseThrow();
        existingTask.setDescription("Updated description");
        existingTask.setPriority(Priority.LOW);
        existingTask.setStatus(TaskStatus.COMPLETED);

        // When
        TaskManagement savedTask = repository.save(existingTask);

        // Then
        assertEquals("Updated description", savedTask.getDescription());
        assertEquals(Priority.LOW, savedTask.getPriority());
        assertEquals(TaskStatus.COMPLETED, savedTask.getStatus());

        // Confirm from repository
        Optional<TaskManagement> foundTask = repository.findById(1L);
        assertTrue(foundTask.isPresent());
        assertEquals("Updated description", foundTask.get().getDescription());
    }


    @Test
    @DisplayName("Should handle concurrent access safely")
    void shouldHandleConcurrentAccessSafely() throws InterruptedException {
        // Given
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];

        // When - Create multiple threads that save tasks concurrently
        for (int i = 0; i < threadCount; i++) {
            final int taskIndex = i;
            threads[i] = new Thread(() -> {
                TaskManagement task = TaskManagement.builder()
                        .referenceId(1000L + taskIndex)
                        .referenceType(ReferenceType.ORDER)
                        .task(Task.CREATE_INVOICE)
                        .description("Concurrent task " + taskIndex)
                        .assigneeId(100L + taskIndex)
                        .status(TaskStatus.COMPLETED)
                        .priority(Priority.MEDIUM)
                        .taskDeadlineTime(System.currentTimeMillis() + 86400000)
                        .build();
                repository.save(task);
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        List<TaskManagement> allTasks = repository.findAll();
        assertEquals(6 + threadCount, allTasks.size()); // 6 seed + 5 new tasks

        // Verify all new tasks were saved correctly
        for (int i = 0; i < threadCount; i++) {
            long expectedReferenceId = 1000L + i;
            int finalI = i;
            assertTrue(allTasks.stream().anyMatch(task ->
                    task.getReferenceId().equals(expectedReferenceId) &&
                            task.getDescription().equals("Concurrent task " + finalI)));
        }
    }

    @Test
    @DisplayName("Should return empty list when no tasks match criteria")
    void shouldReturnEmptyListWhenNoTasksMatchCriteria() {
        // When
        List<TaskManagement> tasksByReference = repository.findByReferenceIdAndReferenceType(999L, ReferenceType.ORDER);
        List<TaskManagement> tasksByAssignee = repository.findByAssigneeIdIn(List.of(999L));

        // Then
        assertTrue(tasksByReference.isEmpty());
        assertTrue(tasksByAssignee.isEmpty());
    }

    @Test
    @DisplayName("Should verify seed data contains expected bug scenarios")
    void shouldVerifySeedDataContainsExpectedBugScenarios() {
        // Verify Bug #1 scenario - duplicate tasks
        List<TaskManagement> duplicateTasks = repository.findByReferenceIdAndReferenceType(201L, ReferenceType.ENTITY);
        assertEquals(2, duplicateTasks.size());
        assertTrue(duplicateTasks.stream().allMatch(task ->
                task.getTask().equals(Task.ASSIGN_CUSTOMER_TO_SALES_PERSON)));

        // Verify Bug #2 scenario - cancelled task
        Optional<TaskManagement> cancelledTask = repository.findById(6L);
        assertTrue(cancelledTask.isPresent());
        assertEquals(TaskStatus.CANCELLED, cancelledTask.get().getStatus());
        assertEquals(Task.COLLECT_PAYMENT, cancelledTask.get().getTask());
    }
}
