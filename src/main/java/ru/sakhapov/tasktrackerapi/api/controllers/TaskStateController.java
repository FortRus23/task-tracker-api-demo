package ru.sakhapov.tasktrackerapi.api.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sakhapov.tasktrackerapi.api.controllers.helpers.ControllerHelper;
import ru.sakhapov.tasktrackerapi.api.dto.AckDto;
import ru.sakhapov.tasktrackerapi.api.dto.TaskStateDto;
import ru.sakhapov.tasktrackerapi.api.dto.taskStateControllerDto.TaskStateCreateDto;
import ru.sakhapov.tasktrackerapi.api.dto.taskStateControllerDto.TaskStateUpdateDto;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.BadRequestException;
import ru.sakhapov.tasktrackerapi.api.factories.TaskStateDtoFactory;
import ru.sakhapov.tasktrackerapi.store.entities.ProjectEntity;
import ru.sakhapov.tasktrackerapi.store.entities.TaskStateEntity;
import ru.sakhapov.tasktrackerapi.store.repositories.TaskStateRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api")
public class TaskStateController {

    TaskStateRepository taskStateRepository;
    TaskStateDtoFactory taskStateDtoFactory;
    ControllerHelper controllerHelper;


    /**
     * Получить все TaskState по ID проекта
     */
    @GetMapping("/projects/{project_id}/task-states")
    public List<TaskStateDto> getTaskStates(@PathVariable("project_id") Long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project
                .getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());
    }

    /**
     * Создание нового TaskState
     */
    @PostMapping("/projects/{project_id}/task-states")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStateDto createTaskState(@PathVariable("project_id") Long projectId,
                                        @Valid @RequestBody TaskStateCreateDto dto) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        boolean exists = project.getTaskStates().stream()
                .anyMatch(state -> state.getName().equalsIgnoreCase(dto.getName()));

        if (exists) {
            throw new BadRequestException(String.format("Task state with name '%s' already exists", dto.getName()));
        }

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity
                        .builder()
                        .name(dto.getName())
                        .project(project)
                        .build()
        );

        final TaskStateEntity savedTaskState = taskStateRepository.save(taskState);

        return taskStateDtoFactory.makeTaskStateDto(savedTaskState);
    }

    /**
     * Обновление TaskState по ID
     */
    @PatchMapping("/task-states/{task_state_id}")
    public TaskStateDto updateTaskState(@PathVariable("task_state_id") Long taskStateId,
                                        @Valid @RequestBody TaskStateUpdateDto dto) {

        TaskStateEntity taskStateEntity = controllerHelper.getTaskStateOrThrowException(taskStateId);

        taskStateRepository
                .findTaskStateEntityByProjectIdAndNameContainsIgnoreCase(taskStateEntity.getProject().getId(), dto.getName())
                .filter(anotherTaskState -> !anotherTaskState.getId().equals(taskStateId))
                .ifPresent(anotherTaskState -> {
                    throw new BadRequestException(String.format("Task state '%s' already exists", dto.getName()));
                });

        taskStateEntity.setName(dto.getName());

        taskStateEntity = taskStateRepository.save(taskStateEntity);

        return taskStateDtoFactory.makeTaskStateDto(taskStateEntity);
    }

    /**
     * Удаление TaskState по ID
     */
    @DeleteMapping("/task-states/{task_state_id}")
    public AckDto deleteTaskState(@PathVariable(name = "task_state_id") Long taskStateId) {

        TaskStateEntity changeTaskState = controllerHelper.getTaskStateOrThrowException(taskStateId);

        taskStateRepository.delete(changeTaskState);

        return AckDto.builder().answer(true).build();
    }
}
