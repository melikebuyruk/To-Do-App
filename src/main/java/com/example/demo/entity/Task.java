package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Schema(
    name = "Task",
    description = "Yapılacak iş kaydı (reaktif CRUD için temel model). " +
                  "POST'ta yalnızca 'title', 'description' ve 'status' gönderilir; " +
                  "'id' ve 'creationDate' sunucuda atanır."
)
@Table("tasks")
public class Task {

    @Schema(description = "Benzersiz kimlik", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    private String id;

    @Schema(description = "Görev başlığı", example = "Alışveriş yap", required = true)
    private String title;

    @Schema(description = "Görev detayı", example = "Market için ekmek ve süt alınacak")
    private String description;

    @Schema(
        description = "Oluşturulma zamanı (ISO-8601; sunucu tarafından atanır)",
        format = "date-time",
        example = "2025-08-11T14:30:00",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    @Column("creation_date")
    private LocalDateTime creationDate;

    @Schema(description = "Görev durumu (enum: TODO, IN_PROGRESS, DONE)", example = "TODO")
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
