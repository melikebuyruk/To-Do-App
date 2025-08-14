package com.example.demo.mapper;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task fromCreate(TaskCreateRequest req) {
        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        if (req.getStatus() != null)
            t.setStatus(TaskStatus.valueOf(req.getStatus()));
        t.setAssigneeId(req.getAssigneeId());
        return t;
    }

    public Task merge(Task t, TaskUpdateRequest req) {
        if (req.getTitle() != null && !req.getTitle().isBlank())
            t.setTitle(req.getTitle());
        if (req.getDescription() != null && !req.getDescription().isBlank())
            t.setDescription(req.getDescription());
        if (req.getStatus() != null && !req.getStatus().isBlank())
            t.setStatus(TaskStatus.valueOf(req.getStatus()));
        if (req.getAssigneeId() != null && !req.getAssigneeId().isBlank())
            t.setAssigneeId(req.getAssigneeId());
        return t;
    }

    public TaskDto toDto(Task t) {
        TaskDto d = new TaskDto();
        d.setId(t.getId());
        d.setTitle(t.getTitle());
        d.setDescription(t.getDescription());
        d.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
        d.setAssigneeId(t.getAssigneeId());
        return d;
    }
}
