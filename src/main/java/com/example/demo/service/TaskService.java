package com.example.demo.service;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.entity.Task;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository tasks;
    private final UserRepository users;
    private final TaskMapper mapper;

    public TaskService(TaskRepository tasks, UserRepository users, TaskMapper mapper) {
        this.tasks = tasks;
        this.users = users;
        this.mapper = mapper;
    }

    // ------- Reactive CRUD -------

    public Flux<TaskDto> list() {
        return tasks.findAll().map(mapper::toDto);
    }

    public Mono<TaskDto> get(String id) {
        return tasks.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(mapper::toDto);
    }

    public Mono<TaskDto> create(TaskCreateRequest req) {
        Task t = mapper.fromCreate(req);
        return tasks.save(t).map(mapper::toDto);
    }

    public Mono<TaskDto> update(String id, TaskUpdateRequest req) {
        return tasks.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(t -> tasks.save(mapper.merge(t, req)))
                .map(mapper::toDto);
    }

    public Mono<Void> delete(String id) {
        return tasks.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(tasks::delete);
    }

    public Mono<TaskDto> assign(String taskId, String assigneeId) {
        if (assigneeId == null || assigneeId.isBlank())
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "assigneeId is blank"));

        return users.existsById(assigneeId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "user")))
                .then(tasks.findById(taskId)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "task"))))
                .flatMap(t -> {
                    t.setAssigneeId(assigneeId);
                    return tasks.save(t);
                })
                .map(mapper::toDto);
    }

    public Mono<TaskDto> unassign(String taskId) {
        return tasks.findById(taskId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(t -> {
                    t.setAssigneeId(null);
                    return tasks.save(t);
                })
                .map(mapper::toDto);
    }

    @Async("taskAssignExecutor")
    public CompletableFuture<TaskDto> assignAsync(String taskId, String assigneeId) {
        log.info("assignAsync called on thread {}", Thread.currentThread().getName());
        return assign(taskId, assigneeId).toFuture();
    }

    @Async("taskAssignExecutor")
    public CompletableFuture<TaskDto> unassignAsync(String taskId) {
        log.info("unassignAsync called on thread {}", Thread.currentThread().getName());
        return unassign(taskId).toFuture();
    }

    @Async("taskAssignExecutor")
    public CompletableFuture<TaskDto> assignAsyncWithDelay(String taskId, String assigneeId, long delayMs) {
        log.info("assignAsyncWithDelay({}) sleeping {} ms on {}", taskId, delayMs, Thread.currentThread().getName());
        return Mono.delay(Duration.ofMillis(delayMs)).then(assign(taskId, assigneeId)).toFuture();
    }
}
