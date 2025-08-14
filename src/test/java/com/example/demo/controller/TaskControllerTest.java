package com.example.demo.controller;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskDto;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.service.TaskService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(controllers = TaskController.class)
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
        t1.setStatus("OPEN");
        t1.setAssigneeId(null);

        t2 = new TaskDto();
        t2.setId("t2");
        t2.setTitle("Pay bills");
        t2.setDescription("Electricity & water");
        t2.setStatus("IN_PROGRESS");
        t2.setAssigneeId("u1");
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
                .jsonPath("$[1].assigneeId").isEqualTo("u1");
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
    @DisplayName("POST /tasks -> 200 + created dto")
    void createTask() {
        Mockito.when(taskService.create(any(TaskCreateRequest.class))).thenReturn(Mono.just(t1));

        String body = """
                { "title":"Buy milk", "description":"2L", "status":"OPEN" }
                """;

        webTestClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("t1")
                .jsonPath("$.status").isEqualTo("OPEN");
    }

    @Test
    @DisplayName("PUT /tasks/{id} -> 200 + updated dto")
    void updateTask() {
        Mockito.when(taskService.update(eq("t2"), any(TaskUpdateRequest.class))).thenReturn(Mono.just(t2));

        String body = """
                { "title":"Pay bills", "status":"IN_PROGRESS" }
                """;

        webTestClient.put()
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
    @DisplayName("PUT /tasks/{id}/assignee -> 200 + dto")
    void assignTask() {
        Mockito.when(taskService.assign("t1", "u1")).thenReturn(Mono.just(t2));

        String body = """
                { "assigneeId":"u1" }
                """;

        webTestClient.put()
                .uri("/tasks/{id}/assignee", "t1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("t2")
                .jsonPath("$.assigneeId").isEqualTo("u1");
    }

    @Test
    @DisplayName("DELETE /tasks/{id}/assignee -> 200 + dto")
    void unassignTask() {
        TaskDto unassigned = new TaskDto();
        unassigned.setId("t2");
        unassigned.setTitle("Pay bills");
        unassigned.setDescription("Electricity & water");
        unassigned.setStatus("IN_PROGRESS");
        unassigned.setAssigneeId(null);

        Mockito.when(taskService.unassign("t2")).thenReturn(Mono.just(unassigned));

        webTestClient.delete()
                .uri("/tasks/{id}/assignee", "t2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("t2")
                .jsonPath("$.assigneeId").doesNotExist();
    }

    @Test
    @DisplayName("DELETE /tasks/{id} -> 200")
    void deleteTask() {
        Mockito.when(taskService.delete("t1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tasks/{id}", "t1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
