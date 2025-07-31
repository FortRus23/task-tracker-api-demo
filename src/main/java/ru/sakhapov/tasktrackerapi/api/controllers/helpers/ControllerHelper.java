package ru.sakhapov.tasktrackerapi.api.controllers.helpers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sakhapov.tasktrackerapi.api.exceptions.NotFoundException;
import ru.sakhapov.tasktrackerapi.store.entities.ProjectEntity;
import ru.sakhapov.tasktrackerapi.store.entities.TaskStateEntity;
import ru.sakhapov.tasktrackerapi.store.repositories.ProjectRepository;
import ru.sakhapov.tasktrackerapi.store.repositories.TaskStateRepository;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Transactional
public class ControllerHelper {

    ProjectRepository projectRepository;

    TaskStateRepository taskStateRepository;

    public ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository.
                findById(projectId)
                .orElseThrow(() -> new NotFoundException(
                                String.format(
                                        "Project with '%s' this id not found.",
                                        projectId
                                )
                        )
                );
    }

    public TaskStateEntity getTaskStateOrThrowException(Long taskStateId) {
        return taskStateRepository.
                findById(taskStateId)
                .orElseThrow(() -> new NotFoundException(
                                String.format(
                                        "Task state with '%s' this id not found.",
                                        taskStateId
                                )
                        )
                );
    }

}
