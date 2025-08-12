package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Schema(name = "Task")
@Document("tasks")
public class Task {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    private String id;

    @Schema(required = true)
    private String title;

    private String description;

    @Schema(format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime creationDate;

    private TaskStatus status;

    public Task() {}

    public Task(String id, String title, String description, LocalDateTime creationDate, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}
