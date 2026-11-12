package com.example.tasks;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class TaskController {

    public Set<Task> tasks = new HashSet<>();
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/tasks")
    public ResponseEntity<Set<Task>> allTasks(@RequestParam(required = false) Boolean done) {
        if (done == null) {
            return ResponseEntity.ok(tasks);
        }
        Set<Task> filtered = tasks.stream()
            .filter(t -> t.done() != null && t.done().equals(done))
            .collect(Collectors.toSet());
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> singleTask(@PathVariable Long id) {
        Task task = tasks.stream()
            .filter(t -> t.id() == id)
            .findFirst()
            .orElse(null);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {
        tasks.removeIf(t -> t.id() == id);   
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> addTask(@RequestParam String title, @RequestParam Boolean done) {
        Task newTask = new Task(counter.incrementAndGet(), title, done);
        tasks.add(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody(required = false) TaskUpdateRequest updateRequest) {
        Task existingTask = tasks.stream()
            .filter(t -> t.id() == id)
            .findFirst()
            .orElse(null);

        if (existingTask == null) {
            return ResponseEntity.badRequest().build();
        }

        String updatedTitle = (updateRequest != null && updateRequest.title() != null)
            ? updateRequest.title()
            : existingTask.title();
        Boolean updatedDone = (updateRequest != null && updateRequest.done() != null)
            ? updateRequest.done()
            : existingTask.done();

        tasks.remove(existingTask);
        Task updatedTask = new Task(id, updatedTitle, updatedDone);
        tasks.add(updatedTask);
        return ResponseEntity.ok(updatedTask);
    }

}
