# Task Management API — To-Do-App

A simple Spring Boot REST API for managing users and tasks.

## Features
- Create, update, delete, and list users
- Create, update, delete, and list tasks
- Assign tasks to users
- Change task status
- Basic error handling

## Technologies
- Java 17+
- Spring Boot
- Spring Data JPA
- H2 Database (in-memory)
- Gradle
- Docker

## Getting Started

Choose **one** of the following ways to run the app.

### Run with Docker Compose
1. Clone the repository:
   ```bash
   git clone https://github.com/melikebuyruk/To-Do-App.git
   cd To-Do-App

2. Start the application:
   ```bash
   docker compose up
The API will be available at http://localhost:8080

Run with Gradle
1. Clone the repository:
   ```bash
   git clone  https://github.com/melikebuyruk/To-Do-App.git
   cd To-Do-App
2. Build the project:
   ```bash
    ./gradlew build
3. Run the application:
    ```bash
    ./gradlew bootRun

## API Endpoints

### Users
| Method | Endpoint      | Description        |
|--------|---------------|--------------------|
| POST   | `/users`      | Create a new user  |
| GET    | `/users`      | List all users     |
| GET    | `/users/{id}` | Get user by ID     |
| PUT    | `/users/{id}` | Update user        |
| DELETE | `/users/{id}` | Delete user        |

### Tasks
| Method | Endpoint             | Description            |
|--------|----------------------|------------------------|
| POST   | `/tasks`             | Create a new task      |
| GET    | `/tasks`             | List all tasks         |
| GET    | `/tasks/{id}`        | Get task by ID         |
| PUT    | `/tasks/{id}`        | Update task            |
| DELETE | `/tasks/{id}`        | Delete task            |
| PUT    | `/tasks/{id}/assign` | Assign task to a user  |
| PUT    | `/tasks/{id}/status` | Change task status     |

### Tasks (Asynchronous — `CompletableFuture`)

| Method | Endpoint                                                          | Description                       |
|--------|--------------------------------------------------------------------|-----------------------------------|
| PUT    | `/tasks/{id}/assign-async`                                         | Assign task to a user (async)     |
| PUT    | `/tasks/{id}/unassign-async`                                       | Unassign task (async)             |


## Technical Requirements

- **Reactive controller (Spring WebFlux)**  
  → `TaskController` returns `Flux` / `Mono` and wraps async with `Mono.fromFuture(...)`.

- **Controller unit tests**  
  → `src/test/java/.../TaskControllerTest.java`, `UserControllerTest.java`

- **Automatic API documentation (Springdoc / OpenAPI / Swagger)**  
  → Annotations: `@Operation`, `@ApiResponse`, `@Schema`  
  → Swagger UI: `http://localhost:8080/swagger-ui/index.html`  
  → OpenAPI JSON: `http://localhost:8080/v3/api-docs`

- **Data persistence layer**  
  → Repositories: `TaskRepository.java`, `UserRepository.java`  
  → Entities/DTO/Mapper structure in place.

---

## Guidance Items

- **Readable code & best practices**  
  → Clear separation between Controller / Service / DTO / Mapper / Repository layers.

- **Error handling & correct HTTP codes**  
  → `GlobalExceptionHandler.java` for global error handling.  
  → `ResponseStatusException` used for 404/400 cases.  

- **DTO pattern**  
  → `TaskDto.java`, `UserDto.java`  
  → `TaskMapper.java`, `UserMapper.java` (Entity ↔ DTO conversion)

- **Automated docs with request/response examples**  
  → OpenAPI annotations and `@ExampleObject` usage in controllers.

---

## Additional Features

### User entity & one-to-many relation (User ↔ Tasks) + assignment
- `User.java`, `Task.java` (assignee/assigneeId field linking)
- Endpoints:
  - `PUT /tasks/{id}/assignee` (assign, reactive)
  - `DELETE /tasks/{id}/assignee` (unassign, reactive)

### Asynchronous long-running operations (CompletableFuture)
- In `TaskService`:
  - `assignAsync(String taskId, String assigneeId)`
  - `unassignAsync(String taskId)`
- Controller endpoints:
  - `PUT /tasks/{id}/assign-async`
  - `PUT /tasks/{id}/unassign-async`



