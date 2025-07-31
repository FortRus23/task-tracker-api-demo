package ru.sakhapov.tasktrackerapi.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sakhapov.tasktrackerapi.store.entities.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

}
