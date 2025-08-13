package com.example.demo.controller;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @Operation(summary = "List users")
    @GetMapping
    public Flux<UserDto> list() { return service.list(); }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    public Mono<UserDto> get(@PathVariable String id) { return service.get(id); }

    @Operation(summary = "Create user")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserDto> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                { "name": "Ada Lovelace", "email": "ada@example.com" }
                """)
            )
        ) @RequestBody UserCreateRequest req
    ) { return service.create(req); }

    @Operation(summary = "Update user")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserDto> update(
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = """
                { "name": "Ada L.", "email": "ada.l@example.com" }
                """)
            )
        ) @RequestBody UserUpdateRequest req
    ) { return service.update(id, req); }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) { return service.delete(id); }
}
