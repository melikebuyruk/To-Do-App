package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    Flux<Task> findAllByAssigneeId(String assigneeId);
}
