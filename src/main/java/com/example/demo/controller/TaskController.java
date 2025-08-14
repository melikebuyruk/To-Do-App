package com.example.demo.controller;

import com.example.demo.dto.AssigneeRequest;
import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    @Operation(summary = "List tasks")
    @GetMapping
    public Flux<TaskDto> list() { return service.list(); }

    @Operation(summary = "Get task by id")
    @GetMapping("/{id}")
    public Mono<TaskDto> get(@PathVariable String id) { return service.get(id); }

    @Operation(summary = "Create task")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TaskDto> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                { "title":"Report", "description":"Q3", "status":"OPEN", "assigneeId":"u-101" }
                """)
            )
        ) @RequestBody TaskCreateRequest req
    ) { return service.create(req); }

    @Operation(summary = "Update task")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TaskDto> update(
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                { "title":"report", "description":"Q3 updated", "status":"IN_PROGRESS", "assigneeId":"u-101" }
                """)
            )
        ) @RequestBody TaskUpdateRequest req
    ) { return service.update(id, req); }

    @Operation(summary = "Assign task")
    @PutMapping(value = "/{id}/assignee", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TaskDto> assign(
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                { "assigneeId":"u-101" }
                """)
            )
        ) @RequestBody AssigneeRequest body
    ) { return service.assign(id, body.getAssigneeId()); }

    @Operation(summary = "Unassign task")
    @DeleteMapping("/{id}/assignee")
    public Mono<TaskDto> unassign(@PathVariable String id) { return service.unassign(id); }

    @Operation(summary = "Delete task")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) { return service.delete(id); }
}
