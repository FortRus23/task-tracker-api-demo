package ru.sakhapov.tasktrackerapi.api.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sakhapov.tasktrackerapi.api.controllers.helpers.ControllerHelper;
import ru.sakhapov.tasktrackerapi.api.dto.AckDto;
import ru.sakhapov.tasktrackerapi.api.dto.TaskStateDto;
import ru.sakhapov.tasktrackerapi.api.dto.taskStateControllerDto.TaskStateCreateDto;
import ru.sakhapov.tasktrackerapi.api.dto.taskStateControllerDto.TaskStateUpdateDto;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.BadRequestException;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.NotFoundException;
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
public class TaskStateController {

    TaskStateRepository taskStateRepository;

    TaskStateDtoFactory taskStateDtoFactory;

    ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/api/projects/{project_id}/task-states";
    public static final String CREATE_TASK_STATE = "/api/projects/{project_id}/task-states";
    public static final String UPDATE_TASK_STATE = "/api/task-states/{task_state_id}";
    public static final String DELETE_TASK_STATE = "/api/task-states/{task_state_id}";


    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDto> getTaskStates(@PathVariable("project_id") Long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project
                .getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());
    }

    @PostMapping(CREATE_TASK_STATE)
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

    @PatchMapping(UPDATE_TASK_STATE)
    public TaskStateDto updateTaskState(@PathVariable("task_state_id") Long taskStateId,
                                        @Valid @RequestBody TaskStateUpdateDto dto) {

        TaskStateEntity taskStateEntity = getTaskStateOrThrowException(taskStateId);

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

    @DeleteMapping(DELETE_TASK_STATE)
    public AckDto deleteTaskState(@PathVariable(name = "task_state_id") Long taskStateId) {

        TaskStateEntity changeTaskState = getTaskStateOrThrowException(taskStateId);

        taskStateRepository.delete(changeTaskState);

        return AckDto.builder().answer(true).build();
    }

    private TaskStateEntity getTaskStateOrThrowException(Long taskStateId) {

        return taskStateRepository
                .findById(taskStateId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Task state this '%s' can't be found", taskStateId))
                );
    }


}
