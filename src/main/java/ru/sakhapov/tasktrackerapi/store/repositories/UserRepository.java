package ru.sakhapov.tasktrackerapi.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sakhapov.tasktrackerapi.store.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}
