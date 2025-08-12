package com.example.demo.service;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class TaskService {

  private final TaskRepository repo;

  public TaskService(TaskRepository repo) {
    this.repo = repo;
  }

  public Flux<TaskDto> getAll() {
    return repo.findAll().map(TaskMapper::toDto);
  }

  public Mono<TaskDto> getById(String id) {
    return repo.findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Task not found")))
        .map(TaskMapper::toDto);
  }

  public Mono<TaskDto> create(TaskCreateRequest req) {
    if (req.getTitle() == null || req.getTitle().isBlank()) {
      return Mono.error(new IllegalArgumentException("title is required"));
    }
    Task t = new Task();
    t.setTitle(req.getTitle());
    t.setDescription(req.getDescription());
    t.setStatus(parseStatusOrDefault(req.getStatus(), TaskStatus.TODO));
    t.setCreationDate(LocalDateTime.now());
    return repo.save(t).map(TaskMapper::toDto);
  }

  public Mono<TaskDto> update(String id, TaskUpdateRequest req) {
    return repo.findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Task not found")))
        .flatMap(existing -> {
          if (req.getTitle() != null) existing.setTitle(req.getTitle());
          if (req.getDescription() != null) existing.setDescription(req.getDescription());
          if (req.getStatus() != null) {
            existing.setStatus(parseStatusOrDefault(req.getStatus(), existing.getStatus()));
          }
          return repo.save(existing);
        })
        .map(TaskMapper::toDto);
  }

  public Mono<Void> delete(String id) {
    return repo.findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Task not found")))
        .flatMap(repo::delete);
  }

  private static TaskStatus parseStatusOrDefault(String raw, TaskStatus def) {
    if (raw == null || raw.isBlank()) return def;
    try {
      return TaskStatus.valueOf(raw.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      return def;
    }
  }
}
