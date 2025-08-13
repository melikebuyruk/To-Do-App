package com.example.demo.controller;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.service.TaskService;
import com.example.demo.web.GlobalExceptionHandler; // <- remove if you don't have this class
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(controllers = TaskController.class)
@Import(GlobalExceptionHandler.class) // <- remove this line if you don't have a global handler
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TaskService taskService;

    private TaskDto t1;
    private TaskDto t2;

    @BeforeEach
    void setUp() {
        t1 = new TaskDto();
        t1.setId("t1");
        t1.setTitle("Buy milk");
        t1.setDescription("2L");
        t1.setStatus("TODO");
        t1.setAssignedUserId(null);

        t2 = new TaskDto();
        t2.setId("t2");
        t2.setTitle("Pay bills");
        t2.setDescription("Electricity & water");
        t2.setStatus("IN_PROGRESS");
        t2.setAssignedUserId("u1");
    }

    @Test
    @DisplayName("GET /tasks -> 200 + list")
    void listTasks() {
        Mockito.when(taskService.list()).thenReturn(Flux.just(t1, t2));

        webTestClient.get()
            .uri("/tasks")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$[0].id").isEqualTo("t1")
            .jsonPath("$[0].title").isEqualTo("Buy milk")
            .jsonPath("$[1].id").isEqualTo("t2")
            .jsonPath("$[1].assignedUserId").isEqualTo("u1");
    }

    @Test
    @DisplayName("GET /tasks/{id} -> 200 + item")
    void getTask() {
        Mockito.when(taskService.get("t1")).thenReturn(Mono.just(t1));

        webTestClient.get()
            .uri("/tasks/{id}", "t1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo("t1")
            .jsonPath("$.title").isEqualTo("Buy milk");
    }

    @Test
    @DisplayName("POST /tasks -> 201? (200 if service returns 200) + created dto")
    void createTask() {
        Mockito.when(taskService.create(any(TaskCreateRequest.class))).thenReturn(Mono.just(t1));

        String body = """
            {
              "title": "Buy milk",
              "description": "2L",
              "status": "TODO"
            }
            """;

        webTestClient.post()
            .uri("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk() // change to isCreated() if you set response code 201 in controller later
            .expectBody()
            .jsonPath("$.id").isEqualTo("t1")
            .jsonPath("$.status").isEqualTo("TODO");
    }

    @Test
    @DisplayName("PATCH /tasks/{id} -> 200 + updated dto")
    void updateTask() {
        Mockito.when(taskService.update(eq("t2"), any(TaskUpdateRequest.class))).thenReturn(Mono.just(t2));

        String body = """
            {
              "title": "Pay bills",
              "status": "IN_PROGRESS"
            }
            """;

        webTestClient.patch()
            .uri("/tasks/{id}", "t2")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo("t2")
            .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("DELETE /tasks/{id} -> 204")
    void deleteTask() {
        Mockito.when(taskService.delete("t1")).thenReturn(Mono.empty());

        webTestClient.delete()
            .uri("/tasks/{id}", "t1")
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("POST /tasks/{taskId}/assign/{userId} -> 200 + dto")
    void assignTask() {
        Mockito.when(taskService.assignToUser("t1", "u1")).thenReturn(Mono.just(t2)); // assume service returns updated dto

        webTestClient.post()
            .uri("/tasks/{taskId}/assign/{userId}", "t1", "u1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo("t2")
            .jsonPath("$.assignedUserId").isEqualTo("u1");
    }

    @Test
    @DisplayName("POST /tasks/{taskId}/unassign -> 200 + dto")
    void unassignTask() {
        TaskDto unassigned = new TaskDto();
        unassigned.setId("t2");
        unassigned.setTitle("Pay bills");
        unassigned.setDescription("Electricity & water");
        unassigned.setStatus("IN_PROGRESS");
        unassigned.setAssignedUserId(null);

        Mockito.when(taskService.unassign("t2")).thenReturn(Mono.just(unassigned));

        webTestClient.post()
            .uri("/tasks/{taskId}/unassign", "t2")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo("t2")
            .jsonPath("$.assignedUserId").doesNotExist(); // null → often omitted by Jackson; if included, change to .isEqualTo(null)
    }
}
