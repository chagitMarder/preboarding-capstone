package com.example.tasks;

import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    public TaskCount getTaskCount(Collection<Task> tasks) {
        if (tasks == null) {
            return new TaskCount(0, 0);
        }
        long total = tasks.size();
        long done = tasks.stream()
                .filter(t -> Boolean.TRUE.equals(t.done()))
                .count();
        return new TaskCount(total, done);
    }
}
