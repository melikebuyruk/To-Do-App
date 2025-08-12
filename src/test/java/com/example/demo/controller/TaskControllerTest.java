package com.example.demo.controller;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.service.TaskService;
import com.example.demo.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(controllers = TaskController.class)
@Import(GlobalExceptionHandler.class) 
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TaskService taskService;

    private TaskDto d1;
    private TaskDto d2;

    @BeforeEach
    void setUp() {
        d1 = new TaskDto();
        d1.setId("1");
        d1.setTitle("Shopping");
        d1.setDescription("Buy bread and milk");
        d1.setCreationDate(LocalDateTime.of(2025, 8, 9, 20, 0));
        d1.setStatus("TODO");

        d2 = new TaskDto();
        d2.setId("2");
        d2.setTitle("Workout");
        d2.setDescription("30 min run");
        d2.setCreationDate(LocalDateTime.of(2025, 8, 9, 20, 5));
        d2.setStatus("IN_PROGRESS");
    }

    @Test
    @DisplayName("GET /tasks -> 200 & list returns")
    void getAll_shouldReturnList() {
        Mockito.when(taskService.getAll()).thenReturn(Flux.just(d1, d2));

        webTestClient.get()
                .uri("/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[0].title").isEqualTo("Shopping")
                .jsonPath("$[1].id").isEqualTo("2")
                .jsonPath("$[1].status").isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("GET /tasks/{id} -> 200 & returns item")
    void getById_found_shouldReturnItem() {
        Mockito.when(taskService.getById("1")).thenReturn(Mono.just(d1));

        webTestClient.get()
                .uri("/tasks/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.title").isEqualTo("Shopping")
                .jsonPath("$.status").isEqualTo("TODO");
    }

    @Test
    @DisplayName("GET /tasks/{id} -> 404")
    void getById_notFound_shouldReturn404() {
        Mockito.when(taskService.getById("99")).thenReturn(Mono.error(new IllegalArgumentException("Task not found")));

        webTestClient.get()
                .uri("/tasks/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /tasks -> 201 & returns created task")
    void create_shouldPersistAndReturn201() {
        TaskDto saved = new TaskDto();
        saved.setId("10");
        saved.setTitle("New Task");
        saved.setDescription("Description");
        saved.setCreationDate(LocalDateTime.of(2025, 8, 9, 21, 0));
        saved.setStatus("TODO");

        Mockito.when(taskService.create(any(TaskCreateRequest.class))).thenReturn(Mono.just(saved));

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
                .jsonPath("$.id").isEqualTo("10")
                .jsonPath("$.title").isEqualTo("New Task")
                .jsonPath("$.status").isEqualTo("TODO");
    }

    @Test
    @DisplayName("POST /tasks -> 400 (blank title)")
    void create_blankTitle_shouldReturn400() {
        Mockito.when(taskService.create(any(TaskCreateRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("title is required")));

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
        TaskDto updated = new TaskDto();
        updated.setId("1");
        updated.setTitle("Updated");
        updated.setDescription("New description");
        updated.setCreationDate(LocalDateTime.of(2025, 8, 9, 20, 0));
        updated.setStatus("IN_PROGRESS");

        Mockito.when(taskService.update(eq("1"), any(TaskUpdateRequest.class))).thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {"title":"Updated","description":"New description","status":"IN_PROGRESS"}
                """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.title").isEqualTo("Updated")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("PUT /tasks/{id} -> 404 (not found)")
    void update_notFound_shouldReturn404() {
        Mockito.when(taskService.update(eq("123"), any(TaskUpdateRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Task not found")));

        webTestClient.put()
                .uri("/tasks/123")
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
        Mockito.when(taskService.delete("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tasks/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /tasks/{id} -> 404 (not found)")
    void delete_notFound_shouldReturn404() {
        Mockito.when(taskService.delete("1"))
                .thenReturn(Mono.error(new IllegalArgumentException("Task not found")));

        webTestClient.delete()
                .uri("/tasks/1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
