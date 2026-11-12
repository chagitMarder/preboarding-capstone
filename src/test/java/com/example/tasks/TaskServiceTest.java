package com.example.tasks;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskServiceTest {

    private final TaskService taskService = new TaskService();

    @Test
    void getTaskCountShouldReturnCorrectCounts() {
        Set<Task> tasks = Set.of(
            new Task(1, "Task 1", true),
            new Task(2, "Task 2", false),
            new Task(3, "Task 3", true)
        );

        TaskCount result = taskService.getTaskCount(tasks);

        assertEquals(3, result.total());
        assertEquals(2, result.done());
    }

    @Test
    void getTaskCountShouldHandleEmptyTasks() {
        TaskCount result = taskService.getTaskCount(Set.of());

        assertEquals(0, result.total());
        assertEquals(0, result.done());
    }

    @Test
    void getTaskCountShouldHandleNullTasks() {
        TaskCount result = taskService.getTaskCount(null);

        assertEquals(0, result.total());
        assertEquals(0, result.done());
    }

    @Test
    void getTaskCountShouldHandleNullDoneValue() {
        List<Task> tasks = List.of(
            new Task(1, "Task 1", null),
            new Task(2, "Task 2", true)
        );

        TaskCount result = taskService.getTaskCount(tasks);

        assertEquals(2, result.total());
        assertEquals(1, result.done());
    }
}
