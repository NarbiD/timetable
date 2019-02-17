package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ua.knu.timetable.bot.TelegramBot;
import ua.knu.timetable.bot.WeekParityChecker;
import ua.knu.timetable.bot.menu.TelegramBotKeyboardFactory;

import java.io.IOException;

@Service
public class TelegramBotService {

    @Autowired
    public TelegramBotService(TimetableService timetableService) throws IOException, TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        TelegramBot bot = new TelegramBot(new TelegramBotKeyboardFactory(), timetableService);
        WeekParityChecker.INSTANCE.setFirstDay("21/01/19");
        botsApi.registerBot(bot);
    }
}
