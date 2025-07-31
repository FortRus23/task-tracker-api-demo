package ru.sakhapov.tasktrackerapi.api.factories;

import org.springframework.stereotype.Component;
import ru.sakhapov.tasktrackerapi.api.dto.TaskDto;
import ru.sakhapov.tasktrackerapi.store.entities.TaskEntity;

@Component
public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity entity) {

        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
