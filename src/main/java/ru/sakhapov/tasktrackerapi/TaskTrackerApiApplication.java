package ru.sakhapov.tasktrackerapi;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskTrackerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerApiApplication.class, args);
    }
}
