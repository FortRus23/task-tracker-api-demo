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
import ru.sakhapov.tasktrackerapi.api.dto.ProjectDto;
import ru.sakhapov.tasktrackerapi.api.dto.projectControllerDto.ProjectCreateDto;
import ru.sakhapov.tasktrackerapi.api.dto.projectControllerDto.ProjectUpdateDto;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.BadRequestException;
import ru.sakhapov.tasktrackerapi.api.factories.ProjectDtoFactory;
import ru.sakhapov.tasktrackerapi.store.entities.ProjectEntity;
import ru.sakhapov.tasktrackerapi.store.repositories.ProjectRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/projects")
public class ProjectController {

    ProjectRepository projectRepository;
    ProjectDtoFactory projectDtoFactory;
    ControllerHelper controllerHelper;

    /**
     * Создание нового проекта
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(@Valid @RequestBody ProjectCreateDto dto) {

        projectRepository
                .findByName(dto.getName())
                .ifPresent(project -> {
                    throw new BadRequestException(
                            String.format("Project with name '%s' already exists.", dto.getName())
                    );
                });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(dto.getName())
                        .build()
        );

        project = projectRepository.save(project);

        return projectDtoFactory.makeProjectDto(project);
    }


    /**
     * Обновление названия проекта
     */
    @PatchMapping("/{project_id}")
    public ProjectDto editProject(@PathVariable("project_id") Long projectId,
                                  @Valid @RequestBody ProjectUpdateDto dto) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        projectRepository
                .findByName(dto.getName())
                .filter(elseProject -> !Objects.equals(elseProject.getId(), projectId))
                .ifPresent(elseProject -> {
                    throw new BadRequestException(
                            String.format("Project with name '%s' already exists.", dto.getName())
                    );
                });

        project.setName(dto.getName());
        project = projectRepository.save(project);

        return projectDtoFactory.makeProjectDto(project);
    }

    /**
     * Получение списка всех проектов (опционально — фильтрация по префиксу имени)
     */
    @GetMapping
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false) String prefixName) {

        Optional<String> optionalPrefixName = Optional.ofNullable(prefixName)
                .filter(p -> !p.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectStream.map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    /**
     * Удаление проекта по ID
     */
    @DeleteMapping("/{project_id}")
    public AckDto deleteProject(@PathVariable("project_id") Long projectId) {

        controllerHelper.getProjectOrThrowException(projectId);
        projectRepository.deleteById(projectId);

        return AckDto.makeDefault(true);
    }

}
