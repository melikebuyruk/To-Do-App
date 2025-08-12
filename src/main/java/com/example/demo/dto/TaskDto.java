package com.example.demo.dto;

import java.time.LocalDateTime;

public class TaskDto {
  private String id;
  private String title;
  private String description;
  private LocalDateTime creationDate;
  private String status;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public LocalDateTime getCreationDate() { return creationDate; }
  public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}
