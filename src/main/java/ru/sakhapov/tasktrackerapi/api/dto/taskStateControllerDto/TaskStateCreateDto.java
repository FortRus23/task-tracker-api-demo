package ru.sakhapov.tasktrackerapi.api.dto.taskStateControllerDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStateCreateDto {

    @NotBlank(message = "Task state name can't be empty.")
    String name;
}
