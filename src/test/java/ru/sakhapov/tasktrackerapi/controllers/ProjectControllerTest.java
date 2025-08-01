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
import ru.sakhapov.tasktrackerapi.store.repositories.ProjectRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        projectRepository.deleteAll();
    }

    @Test
    void shouldCreateProjectAndReturn201() throws Exception {
        var requestBody = """
                    {
                      "name": "Test Project"
                    }
                """;

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.id").exists());

        var projects = projectRepository.findAll();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Test Project");
    }

    @Test
    void shouldGetProjects() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateProject() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Initial Project");
        project = projectRepository.saveAndFlush(project);

        mockMvc.perform(patch("/api/projects/" + project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Project\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteProject() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Initial Project");
        project = projectRepository.saveAndFlush(project);

        mockMvc.perform(delete("/api/projects/" + project.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.answer").value(true));
    }
}