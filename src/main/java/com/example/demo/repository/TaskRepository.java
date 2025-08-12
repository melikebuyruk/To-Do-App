package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveCrudRepository<Task, String> {  
    Flux<Task> findAllByStatus(String status); 
}
