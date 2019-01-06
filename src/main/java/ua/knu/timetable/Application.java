package ua.knu.timetable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ua.knu.timetable.bot.TelegramBot;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ua.knu.timetable.repository")
@EntityScan(basePackages = "ua.knu.timetable.model")
@ComponentScan(basePackages = "ua.knu.timetable.service")
@ComponentScan(basePackages = "ua.knu.timetable.controller")
public class Application {
    public static void main(String[] args) throws IOException, TelegramApiRequestException {
        SpringApplication.run(Application.class, args);
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(new TelegramBot());
    }
}
