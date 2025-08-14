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
- Docker (optional)

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

