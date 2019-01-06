package ua.knu.timetable.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TelegramBot extends TelegramLongPollingBot implements messengerBot  {
    private Properties properties;

    public TelegramBot() throws IOException {
        super();
        properties = new Properties();
        final String absolutePath = new File("").getAbsolutePath();
        properties.load(new FileReader(new File(absolutePath + "/src/main/resources/telegramBot.properties")));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            //TODO: send query to controller
        }
    }

    @Override
    public String getBotUsername() {
        return this.properties.getProperty("bot.name");
    }

    @Override
    public String getBotToken() {
        return this.properties.getProperty("bot.token");
    }
}
