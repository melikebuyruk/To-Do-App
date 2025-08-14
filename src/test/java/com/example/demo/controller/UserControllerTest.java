package com.example.demo.controller;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.service.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private UserDto u1;
    private UserDto u2;

    @BeforeEach
    void setUp() {
        u1 = new UserDto();
        u1.setId("u1");
        u1.setName("Ada Lovelace");
        u1.setEmail("ada@example.com");
        u1.setTaskIds(List.of("t1", "t2"));

        u2 = new UserDto();
        u2.setId("u2");
        u2.setName("Grace Hopper");
        u2.setEmail("grace@example.com");
        u2.setTaskIds(List.of());
    }

    @Test
    @DisplayName("GET /users -> 200 + list")
    void listUsers() {
        Mockito.when(userService.list()).thenReturn(Flux.just(u1, u2));

        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("u1")
                .jsonPath("$[0].taskIds[0]").isEqualTo("t1")
                .jsonPath("$[1].id").isEqualTo("u2");
    }

    @Test
    @DisplayName("GET /users/{id} -> 200 + item")
    void getUser() {
        Mockito.when(userService.get("u1")).thenReturn(Mono.just(u1));

        webTestClient.get()
                .uri("/users/{id}", "u1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("u1")
                .jsonPath("$.name").isEqualTo("Ada Lovelace")
                .jsonPath("$.taskIds[1]").isEqualTo("t2");
    }

    @Test
    @DisplayName("POST /users -> 200 + created dto")
    void createUser() {
        Mockito.when(userService.create(any(UserCreateRequest.class))).thenReturn(Mono.just(u1));

        String body = """
                { "name": "Ada Lovelace", "email": "ada@example.com" }
                """;

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("u1")
                .jsonPath("$.email").isEqualTo("ada@example.com");
    }

    @Test
    @DisplayName("PUT /users/{id} -> 200 + updated dto")
    void updateUser() {
        Mockito.when(userService.update(eq("u2"), any(UserUpdateRequest.class))).thenReturn(Mono.just(u2));

        String body = """
                { "name": "Grace H.", "email": "grace@example.com" }
                """;

        webTestClient.put()
                .uri("/users/{id}", "u2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("u2")
                .jsonPath("$.name").isEqualTo("Grace Hopper");
    }

    @Test
    @DisplayName("DELETE /users/{id} -> 200")
    void deleteUser() {
        Mockito.when(userService.delete("u1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/users/{id}", "u1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
