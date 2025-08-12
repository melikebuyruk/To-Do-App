package com.example.demo.controller;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = TaskController.class)
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TaskRepository taskRepository;

    private Task t1;
    private Task t2;

    @BeforeEach
    void setUp() {
        t1 = new Task("1", "Shopping", "Buy bread and milk", 
                LocalDateTime.of(2025, 8, 9, 20, 0), TaskStatus.TODO);

        t2 = new Task("2", "Workout", "30 min run", 
                LocalDateTime.of(2025, 8, 9, 20, 5), TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("GET /tasks -> 200 & list returns")
    void getAll_shouldReturnList() {
        Mockito.when(taskRepository.findAll()).thenReturn(Flux.just(t1, t2));

        webTestClient.get()
                .uri("/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")  // Use String for ID
                .jsonPath("$[0].title").isEqualTo("Shopping")
                .jsonPath("$[1].id").isEqualTo("2")  // Use String for ID
                .jsonPath("$[1].status").isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("GET /tasks/{id} -> 200 & returns item")
    void getById_found_shouldReturnItem() {
        Mockito.when(taskRepository.findById("1")).thenReturn(Mono.just(t1));

        webTestClient.get()
                .uri("/tasks/1")  // Use String in the URI
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")  // Use String for ID
                .jsonPath("$.title").isEqualTo("Shopping")
                .jsonPath("$.status").isEqualTo("TODO");
    }

    @Test
    @DisplayName("GET /tasks/{id} -> 404")
    void getById_notFound_shouldReturn404() {
        Mockito.when(taskRepository.findById("99")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/tasks/99")  // Use String in the URI
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /tasks -> 201 & returns created task")
    void create_shouldPersistAndReturn201() {
        Task saved = new Task("10", "New Task", "Description", 
                LocalDateTime.of(2025, 8, 9, 21, 0), TaskStatus.TODO);

        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(saved));

        webTestClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {"title":"New Task","description":"Description","status":"TODO"}
                """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("10")  // Use String for ID
                .jsonPath("$.title").isEqualTo("New Task")
                .jsonPath("$.status").isEqualTo("TODO");
    }

    @Test
    @DisplayName("POST /tasks -> 400 (blank title)")
    void create_blankTitle_shouldReturn400() {
        webTestClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {"title":"", "description":"desc", "status":"TODO"}
                """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("PUT /tasks/{id} -> 200 & returns updated task")
    void update_shouldModifyAndReturnUpdated() {
        Task existing = new Task("1", "Old", "Old description", 
                LocalDateTime.of(2025, 8, 9, 20, 0), TaskStatus.TODO);
        Task updated = new Task("1", "Updated", "New description", 
                existing.getCreationDate(), TaskStatus.IN_PROGRESS);

        Mockito.when(taskRepository.findById("1")).thenReturn(Mono.just(existing));
        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri("/tasks/1")  // Use String for ID
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {"title":"Updated","description":"New description","status":"IN_PROGRESS"}
                """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")  // Use String for ID
                .jsonPath("$.title").isEqualTo("Updated")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("PUT /tasks/{id} -> 404 (not found)")
    void update_notFound_shouldReturn404() {
        Mockito.when(taskRepository.findById("123")).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/tasks/123")  // Use String in the URI
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {"title":"X"}
                """)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("DELETE /tasks/{id} -> 204")
    void delete_shouldReturn204() {
        Mockito.when(taskRepository.findById("1")).thenReturn(Mono.just(t1));
        Mockito.when(taskRepository.delete(any(Task.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tasks/1")  // Use String for ID
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /tasks/{id} -> 404 (not found)")
    void delete_notFound_shouldReturn404() {
        Mockito.when(taskRepository.findById("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tasks/1")  // Use String for ID
                .exchange()
                .expectStatus().isNotFound();
    }
}
