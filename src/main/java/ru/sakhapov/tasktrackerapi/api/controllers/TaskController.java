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
import ru.sakhapov.tasktrackerapi.api.dto.TaskDto;
import ru.sakhapov.tasktrackerapi.api.dto.taskControllerDto.TaskCreateDto;
import ru.sakhapov.tasktrackerapi.api.dto.taskControllerDto.TaskUpdateDto;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.BadRequestException;
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
@RequestMapping("/api")
public class TaskController {

    TaskRepository taskRepository;
    TaskDtoFactory taskDtoFactory;
    ControllerHelper controllerHelper;

    /**
     * Получить список задач внутри TaskState
     */
    @GetMapping("/task-states/{task_state_id}/tasks")
    public List<TaskDto> getTasks(@PathVariable("task_state_id") Long taskStateId) {

        TaskStateEntity taskState = controllerHelper.getTaskStateOrThrowException(taskStateId);

        return taskState
                .getTasks()
                .stream()
                .map(taskDtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }

    /**
     * Создать новую задачу в TaskState
     */
    @PostMapping("/task-states/{task_state_id}/task")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@PathVariable("task_state_id") Long taskStateId,
                              @Valid @RequestBody TaskCreateDto dto) {

        TaskStateEntity taskState = controllerHelper.getTaskStateOrThrowException(taskStateId);

        boolean taskExists = taskState.getTasks().stream()
                .anyMatch(task -> task.getName().equalsIgnoreCase(dto.getName()));

        if(taskExists) {
            throw new BadRequestException(
                    String.format("Task with name '%s' already exists", dto.getName())
            );
        }

        TaskEntity task = taskRepository.saveAndFlush(
                TaskEntity.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .taskState(taskState)
                        .build()
        );

        final TaskEntity savedTask = taskRepository.save(task);

        return taskDtoFactory.makeTaskDto(savedTask);
    }

    /**
     * Обновить задачу по ID
     */
    @PatchMapping("/task/{task_id}")
    public TaskDto updateTaskState(@PathVariable("task_id") Long taskId,
                                   @Valid @RequestBody TaskUpdateDto dto) {

        TaskEntity taskEntity = controllerHelper.getTaskOrThrowException(taskId);

        taskEntity.setName(dto.getName());

        taskEntity.setDescription(dto.getDescription());

        taskEntity = taskRepository.save(taskEntity);

        return taskDtoFactory.makeTaskDto(taskEntity);
    }


    /**
     * Удалить задачу по ID
     */
    @DeleteMapping("/task/{task_id}")
    public AckDto deleteTask(@PathVariable(name = "task_id") Long taskId) {

        TaskEntity changeTask = controllerHelper.getTaskOrThrowException(taskId);

        taskRepository.delete(changeTask);

        return AckDto.builder().answer(true).build();
    }
}
