package ru.sakhapov.tasktrackerapi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class TaskDto {

    @NonNull
    Long id;

    @NonNull
    String name;

    @NonNull
    String description;

    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
}
