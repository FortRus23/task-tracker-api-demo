package ru.sakhapov.tasktrackerapi.api.dto.taskControllerDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDto {

    @NotBlank(message = "Project name can't be empty.")
    private String name;

    @NotBlank(message = "Description can't be empty.")
    private String description;
}

