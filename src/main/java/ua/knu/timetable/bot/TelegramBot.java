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
import ua.knu.timetable.bot.menu.KeyboardFactory;
import ua.knu.timetable.model.*;
import ua.knu.timetable.service.TimetableService;
import com.vdurmont.emoji.EmojiParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.knu.timetable.bot.TelegramBot.OutputMessage.*;

public class TelegramBot extends TelegramLongPollingSessionBot {
    private final String DEPARTMENT_ATTRIBUTE = "department";
    private final String GROUP_ATTRIBUTE = "group";
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

    private TimetableService timetableService;
    private KeyboardFactory<ReplyKeyboard> keyboardFactory;

    public TelegramBot(KeyboardFactory<ReplyKeyboard> keyboardFactory, TimetableService timetableService) throws IOException {
        super();
        this.keyboardFactory = keyboardFactory;
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
        initOutputMessages(LangCode.UA.getCode());
        initDefaultButtons(LangCode.UA.getCode());
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

    //TODO: Refactoring onUpdateReceived

    @Override
    public void onUpdateReceived(Update update, Optional<Session> session) {
        if (session.isPresent() && update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            String inputText = update.getMessage().getText();
            String inputPrefix ="";
            if (inputText.contains("  ")){
                inputText = update.getMessage().getText().split("  ")[1];
                inputPrefix = update.getMessage().getText().split("  ")[0];
            }
            if (inputText.equals("/start")) {
                start(session.get(), update.getMessage().getChatId());
                showMainMenu(session.get(), message);
            } else if (inputPrefix.equals(EmojiParser.parseToUnicode(":bust_in_silhouette:"))) {
                session.get().setAttribute("teacher", inputText);
                message.setText(outputMessages.get(SELECT_DAY));
                message.setReplyMarkup(keyboardFactory.makeInlineKeyboard(Stream.of(Day.values()).map(Day::getShortName).collect(Collectors.toList()), CALLBACK_PREFIX_DAY_SELECTION + "teacher"));
            } else if (inputPrefix.equals(EmojiParser.parseToUnicode(":spiral_calendar_pad:")) && inputText.equals("Розклад груп")) {
                message.setText(outputMessages.get(SELECT_YEAR));
                String departmentName = session.get().getAttribute(DEPARTMENT_ATTRIBUTE).toString();
                List<String> years = timetableService.findGroupsByDepartment(departmentName).stream()
                        .map(Group::getYearOfStudy).distinct().sorted()
                        .map(year->EmojiParser.parseToUnicode(year + "-й"))
                        .collect(Collectors.toList());
                message.setReplyMarkup(keyboardFactory.makeResizableKeyboard(years));
            } else if (inputPrefix.equals(EmojiParser.parseToUnicode(":spiral_calendar_pad:")) && inputText.equals("Розклад викладача")) {
                message.setText("Оберіть викладача або введіть його прізвище");
                List<String> teacherNames = timetableService
                        .findTeacherByDepartment(session.get().getAttribute(DEPARTMENT_ATTRIBUTE).toString())
                        .stream().limit(30).map(Teacher::getName).collect(Collectors.toList());
                message.setReplyMarkup(keyboardFactory.makeKeyboard(teacherNames, EmojiParser.parseToUnicode(":bust_in_silhouette:"), true));
            } else if (inputPrefix.equals("\u2B05")) {
                showMainMenu(session.get(), message);
            } else if (inputText.matches("^[1-6]-й")) {
                int year = (int)update.getMessage().getText().charAt(0)-48;
                String departmentName = session.get().getAttribute(DEPARTMENT_ATTRIBUTE).toString();
                message.setText(outputMessages.get(SELECT_GROUP));
                List<String> groups = timetableService.findGroupsByDepartmentAndYearOfStudy(departmentName, year).stream()
                        .map(Group::getName)
                        .collect(Collectors.toList());
                message.setReplyMarkup(keyboardFactory.makeResizableKeyboard(groups, EmojiParser.parseToUnicode(":busts_in_silhouette:")));
            } else if (inputPrefix.equals(EmojiParser.parseToUnicode(":busts_in_silhouette:"))) {
                session.get().setAttribute(GROUP_ATTRIBUTE, inputText);
                message.setText(outputMessages.get(SELECT_DAY));
                message.setReplyMarkup(keyboardFactory.makeInlineKeyboard(Stream.of(Day.values()).map(Day::getShortName).collect(Collectors.toList()), CALLBACK_PREFIX_DAY_SELECTION));
            } else if (inputPrefix.equals(EmojiParser.parseToUnicode(":classical_building:"))){
                rememberDepartment(inputText, session.get());
                message.setText(outputMessages.get(SELECT_DEPARTMENT));
                List<String> buttons = Arrays.asList("Розклад груп","Розклад викладача");
                message.setReplyMarkup(keyboardFactory.makeKeyboard(buttons, EmojiParser.parseToUnicode(":spiral_calendar_pad:"), true));
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
                            studentDay_onClick(resp, update.getCallbackQuery().getMessage(), s));
                    break;
                case CALLBACK_PREFIX_DAY_SELECTION + "teacher":
                    session.ifPresent(s ->
                            teacherDay_onClick(resp, update.getCallbackQuery().getMessage(), s));
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

    private void showMainMenu(Session session, SendMessage message) {
        removeSessionAttributes(session);
        message.setText(outputMessages.get(SELECT_DEPARTMENT));
        List<String> departments = timetableService.findAllDepartments().stream().map(Department::getName).collect(Collectors.toList());
        message.setReplyMarkup(keyboardFactory.makeKeyboard(departments, EmojiParser.parseToUnicode(":classical_building:")));
    }

    private void removeSessionAttributes(Session session) {
        ArrayList<String> keys = new ArrayList<>();
        session.getAttributeKeys().forEach(k->keys.add(k.toString()));
        keys.forEach(session::removeAttribute);
    }

    private void rememberDepartment(String departmentName, Session session) {
        session.setAttribute(DEPARTMENT_ATTRIBUTE, departmentName);
    }

    private void studentDay_onClick(String dayName, Message input, Session session) {
        String fullDayName = Day.getByShortName(dayName).getVisibleName();
        if (!input.getText().contains(fullDayName)) {
            String departmentName = session.getAttribute(DEPARTMENT_ATTRIBUTE).toString();
            String groupName = session.getAttribute(GROUP_ATTRIBUTE).toString();

            List<Lesson> lessons = timetableService
                    .findLessonByDepartmentAndGroupAndDay(departmentName, groupName, Day.getByShortName(dayName).toString());

            send(updateTimetableInMessage(input, lessons, fullDayName, CALLBACK_PREFIX_DAY_SELECTION));
        }
    }

    private void teacherDay_onClick(String dayName, Message input, Session session) {
        String fullDayName = Day.getByShortName(dayName).getVisibleName();
        if (!input.getText().contains(fullDayName)) {
            String departmentName = session.getAttribute(DEPARTMENT_ATTRIBUTE).toString();
            String teacherName = session.getAttribute("teacher").toString();

            List<Lesson> lessons = timetableService
                    .findLessonByDepartmentAndTeacherAndDay(departmentName, teacherName, Day.getByShortName(dayName).toString());

            send(updateTimetableInMessage(input, lessons, fullDayName, CALLBACK_PREFIX_DAY_SELECTION+"teacher"));
        }
    }

    private EditMessageText updateTimetableInMessage(Message message, List<Lesson> lessons, String currentDay, String callbackPrefix) {
        EditMessageText update = new EditMessageText();
        update.setMessageId(message.getMessageId());
        update.setChatId(message.getChatId());
        update.setParseMode("html");
        List<String> days = Stream.of(Day.values()).map(Day::getShortName).collect(Collectors.toList());
        update.setReplyMarkup((InlineKeyboardMarkup) keyboardFactory.makeInlineKeyboard(days,
                callbackPrefix));
        OutputBuilder ob = new OutputBuilder()
                .setDay(currentDay);
        if (!lessons.isEmpty()) {
            ob.setLessons(lessons)
                    .includeSubject(true)
                    .includeSubgroup(true)
                    .includeAudience(true);
            if (callbackPrefix.equals(CALLBACK_PREFIX_DAY_SELECTION)) {
                ob.includeTeacher(true);
            } else if (callbackPrefix.equals(CALLBACK_PREFIX_DAY_SELECTION+"teacher")) {
                ob.includeGroup(true);
            }
        } else {
            ob.setText("Розклад для цього дня відсутній");
        }
        update.setText(ob.build());
        return update;
    }

    private List<String> getDays(Collection<Lesson> lessons) {
        return lessons.stream()
                .map(Lesson::getDay).distinct().sorted()
                .map(Day::getShortName)
                .collect(Collectors.toList());
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
