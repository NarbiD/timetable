package ua.knu.timetable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "ua.knu.timetable.repository")
@EntityScan(basePackages = "ua.knu.timetable.model")
@ComponentScan(basePackages = "ua.knu.timetable.service")
@ComponentScan(basePackages = "ua.knu.timetable.controller")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
