package ua.knu.timetable.bot;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.knu.timetable.bot.menu.MenuFactory;
import ua.knu.timetable.model.Day;
import ua.knu.timetable.model.Lesson;
import ua.knu.timetable.service.TimetableService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static ua.knu.timetable.bot.TelegramBot.DefaultButton.*;
import static ua.knu.timetable.bot.TelegramBot.OutputMessage.*;

public class TelegramBot extends TelegramLongPollingSessionBot {
    private final String DEPARTMENT_ATTRIBUTE = "department";
    private final String CALLBACK_PREFIX_DAY_SELECTION = "daySelection";

    enum OutputMessage {
        WELCOME, SELECT_GROUP, SELECT_DEPARTMENT, SELECT_DAY, SELECTED, CHOICE_CANCELED, SELECT_YEAR,
    }

    enum DefaultButton {
        CHANGE_DEPARTMENT_BUTTON, SUPPORT_BUTTON
    }

    private Map<OutputMessage, String> outputMessages;
    private Map<DefaultButton, String> defaultButtons;

    private Properties botProperties;
    private Properties buttonsProperties;
    private Properties langProperties;

    private OutputFormatter outputFormatter = new OutputFormatter();
    private TimetableService timetableService;
    private MenuFactory<ReplyKeyboard> menuFactory;

    public TelegramBot(MenuFactory<ReplyKeyboard> menuFactory, TimetableService timetableService) throws IOException {
        super();
        this.menuFactory = menuFactory;
        this.timetableService = timetableService;
        loadProperties();
    }

    private void loadProperties() throws IOException {
        final String absolutePath = new File("").getAbsolutePath();
        final String pathToProperties = "/src/main/resources/";

        try (FileReader botPropertiesReader = new FileReader(
                    new File(absolutePath + pathToProperties + "telegramBot.properties"));
            FileReader langPropertiesReader = new FileReader(
                    new File(absolutePath + pathToProperties + "lang.properties"));
            FileReader buttonsPropertiesReader = new FileReader(
                    new File(absolutePath + pathToProperties + "buttons.properties"))) {
            botProperties = new Properties();
            langProperties = new Properties();
            buttonsProperties = new Properties();
            botProperties.load(botPropertiesReader);
            langProperties.load(langPropertiesReader);
            buttonsProperties.load(buttonsPropertiesReader);
        }
        initOutputMessages("ua");
        initDefaultButtons("ua");
    }

    private void initOutputMessages(String langCode) {
        outputMessages = new HashMap<>();
        for (OutputMessage message : OutputMessage.values()) {
            outputMessages.put(message, langProperties.getProperty(langCode + "." + message.toString().toLowerCase()));
        }
    }

    private void initDefaultButtons(String langCode) {
        defaultButtons = new HashMap<>();
        for (DefaultButton button : DefaultButton.values()) {
            defaultButtons.put(button, buttonsProperties.getProperty(langCode + "." + button.toString().toLowerCase()));
        }
    }

    @Override
    public void onUpdateReceived(Update update, Optional<Session> session) {
        if (session.isPresent() && update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            String inputText = update.getMessage().getText();
            if (inputText.equals("/start")) {
                start(session.get(), update.getMessage().getChatId());
                removeSessionAttributes(session.get());
                message.setText(outputMessages.get(SELECT_DEPARTMENT));
                message.setReplyMarkup(menuFactory.makeDepartmentSelectionMenu());
            } else if (inputText.equals(defaultButtons.get(CHANGE_DEPARTMENT_BUTTON))) {
                removeSessionAttributes(session.get());
                message.setText(outputMessages.get(SELECT_DEPARTMENT));
                message.setReplyMarkup(menuFactory.makeDepartmentSelectionMenu());
            } else if (inputText.matches("^[0-6]")) {
                int year = (int)update.getMessage().getText().charAt(0)-48;
                message.setText(outputMessages.get(SELECT_GROUP));
                message.setReplyMarkup(menuFactory.makeGroupSelectionMenu(session.get().getAttribute(DEPARTMENT_ATTRIBUTE).toString(), year));
            } else {
                if (session.get().getAttribute(DEPARTMENT_ATTRIBUTE) != null) {
                    String departmentName = session.get().getAttribute(DEPARTMENT_ATTRIBUTE).toString();
                    session.get().setAttribute("group", inputText);
                    message.setText(outputMessages.get(SELECT_DAY));
                    message.setReplyMarkup(menuFactory.makeDaySelectionMenu(departmentName, inputText, CALLBACK_PREFIX_DAY_SELECTION));
                } else {
                    rememberDepartment(inputText, session.get());
                    message.setReplyMarkup(menuFactory.makeYearSelectionMenu(inputText));
                    message.setText(outputMessages.get(SELECT_YEAR));
                }
            }
            message.setChatId(update.getMessage().getChatId());
            send(message);
        } else if (update.hasCallbackQuery()) {
            String[] callbackData = update.getCallbackQuery().getData().split(":");
            String req = callbackData[0];
            String resp = callbackData[1];
            switch (req) {
                case CALLBACK_PREFIX_DAY_SELECTION:
                    session.ifPresent(s ->
                            day_onClick(resp, update.getCallbackQuery().getMessage(), s));
                    break;

                default:
                    break;
            }
        }
    }

    private void start(Session session, Long chatId) {
        session.setTimeout(Long.MAX_VALUE);
        SendMessage message = new SendMessage();
        OutputMessage messageKey = session.getAttribute(DEPARTMENT_ATTRIBUTE) == null ? WELCOME : CHOICE_CANCELED;
        message.setText(outputMessages.get(messageKey));
        message.setReplyMarkup(new ReplyKeyboardRemove());
        message.setChatId(chatId);
        send(message);
    }

    private void removeSessionAttributes(Session session) {
        ArrayList<String> keys = new ArrayList<>();
        session.getAttributeKeys().forEach(k->keys.add(k.toString()));
        keys.forEach(session::removeAttribute);
    }

    private void rememberDepartment(String departmentName, Session session) {
        session.setAttribute(DEPARTMENT_ATTRIBUTE, departmentName);
    }

    private void day_onClick(String dayName, Message input, Session session) {
        String fullDayName = Day.getByShortName(dayName).getFullName();
        if (!input.getText().contains(fullDayName)) {
            String departmentName = session.getAttribute(DEPARTMENT_ATTRIBUTE).toString();
            String groupName = session.getAttribute("group").toString();

            List<Lesson> lessons = timetableService
                    .findLessonByDepartmentAndGroupAndDay(departmentName, groupName, Day.getByShortName(dayName).toString());

            send(updateTimetableInMessage(input, lessons, fullDayName));
        }
    }

    private EditMessageText updateTimetableInMessage(Message message, List<Lesson> lessons, String day) {
        EditMessageText update = new EditMessageText();
        if (!lessons.isEmpty()) {
            update.setReplyMarkup((InlineKeyboardMarkup)menuFactory.makeDaySelectionMenu(
                    lessons.get(0).getDepartment().getName(),
                    lessons.get(0).getGroup().getName(),
                    CALLBACK_PREFIX_DAY_SELECTION));
            update.setMessageId(message.getMessageId());
            update.setChatId(message.getChatId());
            update.setParseMode("html");
            update.setText(outputFormatter.formatLessonsForADay(lessons, day));
        } else {
            update.setText("Empty"); //TODO: set text
        }
        return update;
    }

    private void send(BotApiMethod<?> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return this.botProperties.getProperty("bot.name");
    }

    @Override
    public String getBotToken() {
        return this.botProperties.getProperty("bot.token");
    }
}
