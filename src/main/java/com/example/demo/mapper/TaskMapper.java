package com.example.demo.mapper;

import com.example.demo.dto.TaskDto;
import com.example.demo.entity.Task;

public final class TaskMapper {
  private TaskMapper(){}

  public static TaskDto toDto(Task t) {
    TaskDto dto = new TaskDto();
    dto.setId(t.getId());
    dto.setTitle(t.getTitle());
    dto.setDescription(t.getDescription());
    dto.setCreationDate(t.getCreationDate());
    dto.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
    return dto;
  }
}
