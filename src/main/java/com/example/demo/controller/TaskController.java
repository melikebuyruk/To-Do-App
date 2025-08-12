package com.example.demo.controller;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(
        summary = "List all tasks",
        description = "Returns all tasks."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Tasks retrieved",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TaskDto.class),
            examples = @ExampleObject(name = "Simple list", value = """
                [
                  {
                    "id": "1",
                    "title": "Buy groceries",
                    "description": "Bread and milk",
                    "creationDate": "2025-08-11T14:30:00",
                    "status": "TODO"
                  },
                  {
                    "id": "2",
                    "title": "Workout",
                    "description": "30 min run",
                    "creationDate": "2025-08-11T15:00:00",
                    "status": "IN_PROGRESS"
                  }
                ]
                """)
        )
    )
    @GetMapping
    public Flux<TaskDto> getAll() {
        return service.getAll();
    }

    @Operation(
        summary = "Get task by ID",
        description = "Returns a single task by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(name = "Simple item", value = """
                    {
                      "id": "1",
                      "title": "Buy groceries",
                      "description": "Bread and milk",
                      "creationDate": "2025-08-11T14:30:00",
                      "status": "TODO"
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public Mono<TaskDto> getById(@PathVariable String id) {
        return service.getById(id)
            .onErrorResume(IllegalArgumentException.class,
                e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")));
    }

    @Operation(
        summary = "Create a new task",
        description = "Creates a task from the given data."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Task created",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TaskDto.class),
            examples = @ExampleObject(name = "Created response", value = """
                {
                  "id": "10",
                  "title": "Prepare slides",
                  "description": "Finish the deck",
                  "creationDate": "2025-08-11T16:45:00",
                  "status": "TODO"
                }
                """)
        )
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TaskCreateRequest.class),
            examples = @ExampleObject(name = "Simple request", value = """
                {
                  "title": "Prepare slides",
                  "description": "Finish the deck",
                  "status": "TODO"
                }
                """)
        )
    )
    public Mono<TaskDto> create(@org.springframework.web.bind.annotation.RequestBody TaskCreateRequest req) {
        return service.create(req);
    }

    @Operation(
        summary = "Update a task",
        description = "Updates an existing task."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task updated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(name = "Updated response", value = """
                    {
                      "id": "1",
                      "title": "Buy groceries (updated)",
                      "description": "Bread, milk, eggs",
                      "creationDate": "2025-08-11T14:30:00",
                      "status": "IN_PROGRESS"
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TaskUpdateRequest.class),
            examples = @ExampleObject(name = "Simple request", value = """
                {
                  "title": "Buy groceries (updated)",
                  "description": "Bread, milk, eggs",
                  "status": "IN_PROGRESS"
                }
                """)
        )
    )
    public Mono<TaskDto> update(@PathVariable String id, @org.springframework.web.bind.annotation.RequestBody TaskUpdateRequest req) {
        return service.update(id, req)
            .onErrorResume(IllegalArgumentException.class,
                e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")));
    }

    @Operation(
        summary = "Delete a task",
        description = "Deletes a task by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Task deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id)
            .onErrorResume(IllegalArgumentException.class,
                e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")));
    }
}
