package com.example.demo.controller;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import com.example.demo.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskRepository repo;

    public TaskController(TaskRepository repo) {
        this.repo = repo;
    }
    @Operation(summary = "List all the tasks")
    @ApiResponse(
        responseCode = "200",
        description = "Liste başarıyla döndü",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                [
                  {
                    "id": 1,
                    "title": "Alışveriş yap",
                    "description": "Ekmek ve süt al",
                    "creationDate": "2025-08-11T14:30:00",
                    "status": "TODO"
                  },
                  {
                    "id": 2,
                    "title": "Koşu",
                    "description": "30 dk",
                    "creationDate": "2025-08-11T15:00:00",
                    "status": "IN_PROGRESS"
                  }
                ]
                """)))
    @GetMapping
    public Flux<Task> getAll() {
        return repo.findAll();
    }

    // --- GET /tasks/{id} ---
    @Operation(summary = "ID ile görev getir")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bulundu", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Task.class),
            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "title": "Alışveriş yap",
                  "description": "Ekmek ve süt al",
                  "creationDate": "2025-08-11T14:30:00",
                  "status": "TODO"
                }
                """)
        )),
        @ApiResponse(responseCode = "404", description = "Task bulunamadı")
    })
    @GetMapping("/{id}")
    public Mono<Task> getById(@PathVariable String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")));
    }

    // --- POST /tasks ---
    @Operation(
        summary = "Yeni görev oluştur",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Task.class),
                examples = @ExampleObject(
                    name = "Örnek istek",
                    value = """
                        {
                          "title": "Alışveriş yap",
                          "description": "Ekmek ve süt al",
                          "status": "TODO"
                        }
                        """
                )
            )
        )
    )
    @ApiResponse(
        responseCode = "201",
        description = "Oluşturuldu",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "id": 10,
                  "title": "Alışveriş yap",
                  "description": "Ekmek ve süt al",
                  "creationDate": "2025-08-11T16:45:00",
                  "status": "TODO"
                }
                """)))


                @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.CREATED)
public Mono<Task> create(@RequestBody Task req) {
    if (req.getTitle() == null || req.getTitle().isBlank()) {
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required"));
    }

    // INSERT zorla
    req.setId(null);

    if (req.getCreationDate() == null) {
        req.setCreationDate(LocalDateTime.now());
    }
    if (req.getStatus() == null) {
        req.setStatus(TaskStatus.TODO);
    }

    return repo.save(req);
}

    // --- PUT /tasks/{id} ---
    @Operation(
        summary = "Görev güncelle",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "title": "Alışveriş yap (güncel)",
                      "description": "Ekmek, süt ve yumurta al",
                      "status": "IN_PROGRESS"
                    }
                    """)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Güncellendi", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "title": "Alışveriş yap (güncel)",
                  "description": "Ekmek, süt ve yumurta al",
                  "creationDate": "2025-08-11T14:30:00",
                  "status": "IN_PROGRESS"
                }
                """)
        )),
        @ApiResponse(responseCode = "404", description = "Task bulunamadı")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Task> update(@PathVariable String id, @RequestBody Task req) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")))
                .flatMap(existing -> {
                    if (req.getTitle() != null) existing.setTitle(req.getTitle());
                    if (req.getDescription() != null) existing.setDescription(req.getDescription());
                    if (req.getStatus() != null) existing.setStatus(req.getStatus());
                    return repo.save(existing);
                });
    }

    @Operation(summary = "Görev sil")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Silindi"),
        @ApiResponse(responseCode = "404", description = "Task bulunamadı")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")))
                .flatMap(repo::delete);
    }
}
