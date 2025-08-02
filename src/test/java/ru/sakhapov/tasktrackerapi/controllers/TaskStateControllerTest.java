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
import ru.sakhapov.tasktrackerapi.store.entities.TaskStateEntity;
import ru.sakhapov.tasktrackerapi.store.repositories.ProjectRepository;
import ru.sakhapov.tasktrackerapi.store.repositories.TaskStateRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TaskStateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStateRepository taskStateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectEntity project;

    @BeforeEach
    void setup() {
        taskStateRepository.deleteAll();
        projectRepository.deleteAll();

        project = new ProjectEntity();
        project.setName("Initial Project");
        project = projectRepository.saveAndFlush(project);
    }

    @Test
    void shouldCreateTaskStateAndReturn201() throws Exception {
        var requestBody = """
                    {
                      "name": "Test Task state"
                    }
                """;

        mockMvc.perform(post("/api/projects/" + project.getId() + "/task-states")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Task state"))
                .andExpect(jsonPath("$.id").exists());

        var projects = taskStateRepository.findAll();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Test Task state");
    }

    @Test
    void shouldGetTaskStates() throws Exception {
        var taskState = new TaskStateEntity();
        taskState.setName("Init");
        taskState.setProject(project);
        taskStateRepository.saveAndFlush(taskState);
        mockMvc.perform(get("/api/projects/" + project.getId() + "/task-states"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateTaskState() throws Exception {
        var taskState = new TaskStateEntity();
        taskState.setName("Init");
        taskState.setProject(project);
        taskState = taskStateRepository.save(taskState);

        mockMvc.perform(patch("/api/task-states/" + taskState.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Task state\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteTaskState() throws Exception {
        var taskState = new TaskStateEntity();
        taskState.setName("Init");
        taskState.setProject(project);
        taskState = taskStateRepository.save(taskState);

        mockMvc.perform(delete("/api/task-states/" + taskState.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.answer").value(true));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        var invalidRequestBody = """
                    {
                      "name": "   "
                    }
                """;

        mockMvc.perform(post("/api/projects/" + project.getId() + "/task-states")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error_description").value("Task state name can't be empty."));
    }
}
