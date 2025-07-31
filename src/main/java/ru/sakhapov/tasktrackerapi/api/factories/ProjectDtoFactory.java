package ru.sakhapov.tasktrackerapi.api.factories;

import org.springframework.stereotype.Component;
import ru.sakhapov.tasktrackerapi.api.dto.ProjectDto;
import ru.sakhapov.tasktrackerapi.store.entities.ProjectEntity;

@Component
public class ProjectDtoFactory {

    public ProjectDto makeProjectDto(ProjectEntity entity){

        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
