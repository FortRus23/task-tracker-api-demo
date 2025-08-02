package ru.sakhapov.tasktrackerapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.sakhapov.tasktrackerapi.store.entities.ProjectEntity;
import ru.sakhapov.tasktrackerapi.store.entities.TaskEntity;
import ru.sakhapov.tasktrackerapi.store.entities.TaskStateEntity;
import ru.sakhapov.tasktrackerapi.store.repositories.ProjectRepository;
import ru.sakhapov.tasktrackerapi.store.repositories.TaskRepository;
import ru.sakhapov.tasktrackerapi.store.repositories.TaskStateRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStateRepository taskStateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectEntity project;

    private TaskStateEntity taskState;

    @BeforeEach
    void setup() {
        taskStateRepository.deleteAll();
        projectRepository.deleteAll();
        taskRepository.deleteAll();

        project = new ProjectEntity();
        project.setName("Initial Project");
        project = projectRepository.saveAndFlush(project);

        taskState = new TaskStateEntity();
        taskState.setName("Initial Task state");
        taskState.setProject(project);
        taskState = taskStateRepository.saveAndFlush(taskState);
    }

    @Test
    void shouldCreateTaskAndReturn201() throws Exception {
        var requestBody = """
                    {
                      "name": "Test Task",
                      "description": "Test task descr"
                    }
                """;

        mockMvc.perform(post("/api/task-states/" + taskState.getId()+ "/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test task descr"))
                .andExpect(jsonPath("$.id").exists());

        var projects = taskRepository.findAll();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Test Task");
        assertThat(projects.get(0).getDescription()).isEqualTo("Test task descr");
    }

    @Test
    void shouldGetTasks() throws Exception {
        var task = new TaskEntity();
        task.setName("Init");
        task.setDescription("Init");
        task.setTaskState(taskState);
        taskRepository.saveAndFlush(task);
        mockMvc.perform(get("/api/task-states/" + taskState.getId()+ "/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        var task = new TaskEntity();
        task.setName("Init");
        task.setDescription("Init");
        task.setTaskState(taskState);
        taskRepository.saveAndFlush(task);

        var requestBody = """
                    {
                      "name": "Updated Task",
                      "description": "Updated descr"
                    }
                """;

        mockMvc.perform(patch("/api/task/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        var task = new TaskEntity();
        task.setName("Init");
        task.setDescription("Init");
        task.setTaskState(taskState);
        taskRepository.saveAndFlush(task);

        mockMvc.perform(delete("/api/task/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.answer").value(true));
    }

}
