package com.example.demo.service;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository users;
    private final TaskRepository tasks;
    private final UserMapper mapper;

    public UserService(UserRepository users, TaskRepository tasks, UserMapper mapper) {
        this.users = users;
        this.tasks = tasks;
        this.mapper = mapper;
    }

    public Flux<UserDto> list() {
        return users.findAll().flatMap(this::withTaskIds);
    }

    public Mono<UserDto> get(String id) {
        return users.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(this::withTaskIds);
    }

    public Mono<UserDto> create(UserCreateRequest req) {
        return users.save(mapper.fromCreate(req)).flatMap(this::withTaskIds);
    }

    public Mono<UserDto> update(String id, UserUpdateRequest req) {
        return users.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(u -> users.save(mapper.merge(u, req)))
                .flatMap(this::withTaskIds);
    }

    public Mono<Void> delete(String id) {
        return users.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(users::delete);
    }

    private Mono<UserDto> withTaskIds(User u) {
        return tasks.findAllByAssigneeId(u.getId())
                .map(t -> t.getId())
                .collectList()
                .map(ids -> {
                    var d = mapper.toDto(u);
                    d.setTaskIds(ids);
                    return d;
                });
    }
}
