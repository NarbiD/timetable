package ua.knu.timetable.bot;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.service.TimetableService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TelegramBot extends TelegramLongPollingSessionBot implements messengerBot  {
    private Properties properties;
    private TimetableService timetableService;

    public TelegramBot(TimetableService timetableService) throws IOException {
        super();
        this.timetableService = timetableService;
        loadProperties();
    }

    private void loadProperties() throws IOException {
        final String absolutePath = new File("").getAbsolutePath();
        properties = new Properties();
        properties.load(new FileReader(new File(absolutePath + "/src/main/resources/telegramBot.properties")));
    }

    @Override
    public void onUpdateReceived(Update update, Optional<Session> session) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            String inputText = update.getMessage().getText();
            if (inputText.equals("/start")) {
                session.ifPresent(s -> start(session.get(), update.getMessage().getChatId()));
                addDepartmentChoosingMenu(message);
            }
            session.ifPresent(s -> {
                        if (s.getAttribute("department") != null && session.get().getAttribute("isGroupMenuShowed") != null) {
                                message.setText(timetableService.findLessonByDepartmentAndGroup(
                                        s.getAttribute("department").toString(), inputText).get(0).getSubject().getName());
                        }
                    });
            sendMessage(message, update.getMessage().getChatId());
        } else if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            String[] queryAndAnswer = callbackQuery.split(":");
            String query = queryAndAnswer[0];
            String answer = queryAndAnswer[1];
            switch (query) {
                case "departmentChoose":
                    session.ifPresent(s -> s.setAttribute("department", answer));
                    SendMessage message = new SendMessage();
                    message.setText("Обрано " + answer +
                            ".\n Тепер оберіть групу.");
                    session.ifPresent(s -> {
                        addGroupChoosingMenu(message, s.getAttribute("department").toString());
                        s.setAttribute("isGroupMenuShowed", true);
                    });
                sendMessage(message, update.getCallbackQuery().getMessage().getChatId());
            }
        }
    }

    private void start(Session session, Long chatId) {
        removeSessionCashe(session);
        SendMessage message = new SendMessage();
        String outputText = session.getAttribute("department") == null ? "Вас вітає бот, що допоможе вам знайти розклад." :
                "Вибір скасовано.";
        message.setText(outputText);
        message.setReplyMarkup(new ReplyKeyboardRemove());
        sendMessage(message, chatId);
    }

    private void sendMessage(SendMessage message, Long chatId) {
        message.setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void removeSessionCashe(Session session) {
        session.getAttributeKeys().forEach(session::removeAttribute);
    }

    private void addDepartmentChoosingMenu(SendMessage message) {
        List<Department> departments = timetableService.findAllDepartments();
        List<String> departmentNames = new ArrayList<>(departments.size());
        for (Department department : departments) {
            departmentNames.add(department.getName());
        }
        message.setText("Оберіть факультет:");
        message.setReplyMarkup(makeInlineKeyboard(departmentNames, "departmentChoose:"));
    }

    private void addGroupChoosingMenu(SendMessage message, String departmentName) {
        List<Group> groups = timetableService.findGroupsByDepartmentName(departmentName);
        List<String> groupNames = new ArrayList<>(groups.size());
        for (Group group : groups) {
            groupNames.add(group.getName());
        }
        message.setReplyMarkup(makeKeyboard(groupNames));
    }

    private ReplyKeyboardMarkup makeKeyboard(List<String> buttonNames) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (String name : buttonNames) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(name));
            keyboard.add(keyboardRow);
        }

        return replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private InlineKeyboardMarkup makeInlineKeyboard(List<String> buttonNames, String callbackPrefix) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (String name : buttonNames) {
            buttons.add(Collections.singletonList(new InlineKeyboardButton()
                    .setText(name)
                    .setCallbackData(callbackPrefix + name)));
        }
        return new InlineKeyboardMarkup().setKeyboard(buttons);
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
