package ru.sakhapov.tasktrackerapi.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sakhapov.tasktrackerapi.api.controllers.helpers.ControllerHelper;
import ru.sakhapov.tasktrackerapi.api.dto.AckDto;
import ru.sakhapov.tasktrackerapi.api.dto.TaskDto;
import ru.sakhapov.tasktrackerapi.api.exceptions.BadRequestException;
import ru.sakhapov.tasktrackerapi.api.exceptions.NotFoundException;
import ru.sakhapov.tasktrackerapi.api.factories.TaskDtoFactory;
import ru.sakhapov.tasktrackerapi.store.entities.TaskEntity;
import ru.sakhapov.tasktrackerapi.store.entities.TaskStateEntity;
import ru.sakhapov.tasktrackerapi.store.repositories.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    TaskRepository taskRepository;

    TaskDtoFactory taskDtoFactory;

    ControllerHelper controllerHelper;

    public static final String GET_TASKS = "/api/task-states/{task_state_id}/tasks";
    public static final String CREATE_TASK = "/api/task-states/{task_state_id}/task";
    public static final String UPDATE_TASK = "/api/task/{task_id}";
    public static final String DELETE_TASK = "/api/task/{task_id}";

    @GetMapping(GET_TASKS)
    public List<TaskDto> getTasks(@PathVariable("task_state_id") Long taskStateId) {

        TaskStateEntity taskState = controllerHelper.getTaskStateOrThrowException(taskStateId);

        return taskState
                .getTasks()
                .stream()
                .map(taskDtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }

    @PostMapping(CREATE_TASK)
    public TaskDto createTask(@PathVariable("task_state_id") Long taskStateId,
                              @RequestParam(name = "task_name") String taskName,
                              @RequestParam(name = "task_description") String taskDescription) {

        if (taskName.isBlank() || taskDescription.isBlank()) {
            throw new BadRequestException("Task name or description can't be empty.");
        }

        TaskStateEntity taskState = controllerHelper.getTaskStateOrThrowException(taskStateId);

        for (TaskEntity task : taskState.getTasks()) {

            if (task.getName().equalsIgnoreCase(taskName)) {
                throw new BadRequestException(String.format("Task with '%s' name already exists", taskName));
            }
        }

        TaskEntity task = taskRepository.saveAndFlush(
                TaskEntity.builder()
                        .name(taskName)
                        .description(taskDescription)
                        .taskState(taskState)
                        .build()
        );

        final TaskEntity savedTask = taskRepository.saveAndFlush(task);

        return taskDtoFactory.makeTaskDto(savedTask);
    }

    @PatchMapping(UPDATE_TASK)
    public TaskDto updateTaskState(@PathVariable("task_id") Long taskId,
                                   @RequestParam(name = "task_name") String taskName,
                                   @RequestParam(name = "task_description") String taskDescription) {

        if (taskName.isBlank() || taskDescription.isBlank()) {
            throw new BadRequestException("Task name or description can't be empty.");
        }

        TaskEntity taskEntity = getTaskOrThrowException(taskId);

        taskEntity.setName(taskName);

        taskEntity.setDescription(taskDescription);

        taskEntity = taskRepository.saveAndFlush(taskEntity);

        return taskDtoFactory.makeTaskDto(taskEntity);
    }

    @DeleteMapping(DELETE_TASK)
    public AckDto deleteTask(@PathVariable(name = "task_id") Long taskId) {

        TaskEntity changeTask = getTaskOrThrowException(taskId);

        taskRepository.delete(changeTask);

        return AckDto.builder().answer(true).build();
    }

    private TaskEntity getTaskOrThrowException(Long taskId) {

        return taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Task with this '%s' can't be found", taskId))
                );
    }

}
