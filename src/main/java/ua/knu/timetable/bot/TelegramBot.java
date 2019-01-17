package ua.knu.timetable.bot;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.knu.timetable.model.Day;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.model.Lesson;
import ua.knu.timetable.service.TimetableService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static ua.knu.timetable.bot.TelegramBot.OutputMessage.*;

public class TelegramBot extends TelegramLongPollingSessionBot implements messengerBot  {
    private final String DEPARTMENT_ATTRIBUTE = "department";
    private final String IS_GROUP_SHOWED_ATTRIBUTE = "isGroupMenuShowed";
    private final String CALLBACK_PREFIX_DAY_CHOOSE = "dayChoose";

    enum OutputMessage {
        WELCOME, SELECT_GROUP, SELECT_DEPARTMENT, SELECT_DAY, SELECTED, CHOICE_CANCELED, SELECT_YEAR
    }

    private Map<OutputMessage, String> outputMessages;

    private Properties botProperties;

    private Properties langProperties;

    private MenuMaker<ReplyKeyboard> menuMaker = new TelegramBotMenuMaker();
    private OutputFormatter outputFormatter = new OutputFormatter();
    private TimetableService timetableService;

    public TelegramBot(TimetableService timetableService) throws IOException {
        super();
        this.timetableService = timetableService;
        loadProperties();
    }

    private void loadProperties() throws IOException {
        final String absolutePath = new File("").getAbsolutePath();
        final String pathToProperties = "/src/main/resources/";
        botProperties = new Properties();
        langProperties = new Properties();
        botProperties.load(new FileReader(new File(absolutePath + pathToProperties + "telegramBot.properties")));
        langProperties.load(new FileReader(new File(absolutePath + pathToProperties + "lang.properties")));
        initOutputMessages("ua");
    }

    private void initOutputMessages(String langCode) {
        outputMessages = new HashMap<>();
        for (OutputMessage message : values()) {
            outputMessages.put(message, langProperties.getProperty(langCode + "." + message.toString().toLowerCase()));
        }
    }

    @Override
    public void onUpdateReceived(Update update, Optional<Session> session) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            String inputText = update.getMessage().getText();
            if (inputText.equals("/start")) {
                session.ifPresent(s -> start(session.get(), update.getMessage().getChatId()));
                addDepartmentChoosingMenu(message);
            } else if (inputText.startsWith("\u2B05")) {
                session.ifPresent(this::removeSessionCache);
                addDepartmentChoosingMenu(message);
            } else if (inputText.matches("^[0-6]")) {
                Integer year = (int)update.getMessage().getText().charAt(0)-48;
                message.setText(outputMessages.get(SELECT_GROUP));
                session.ifPresent(s-> addGroupChoosingMenu(message, s.getAttribute(DEPARTMENT_ATTRIBUTE).toString(), year));
            } else {
                    session.ifPresent(s -> {
                        if (s.getAttribute(DEPARTMENT_ATTRIBUTE) != null) {
                            if (session.get().getAttribute(IS_GROUP_SHOWED_ATTRIBUTE) != null) {
                                s.setAttribute("group", inputText);
                                message.setText(outputMessages.get(SELECT_DAY));
                                message.setReplyMarkup(getDayChoosingMenu(s));
                            }
                        } else {
                            callbackDepartmentChoose(inputText, update.getMessage().getChatId(), s);
                            message.setText(outputMessages.get(SELECT_YEAR));
                        }
                    });
            }
            sendMessage(message, update.getMessage().getChatId());
        } else if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            String[] queryAndAnswer = callbackQuery.split(":");
            String req = queryAndAnswer[0];
            String resp = queryAndAnswer[1];
            switch (req) {
                case CALLBACK_PREFIX_DAY_CHOOSE:
                    session.ifPresent(s ->
                            callbackDayChoose(resp, update.getCallbackQuery().getMessage(), s));
                    break;

                default:
                    break;
            }
        }
    }

    private void start(Session session, Long chatId) {
        removeSessionCache(session);
        SendMessage message = new SendMessage();
        OutputMessage messageKey = session.getAttribute(DEPARTMENT_ATTRIBUTE) == null ? WELCOME : CHOICE_CANCELED;
        String outputText = outputMessages.get(messageKey);
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

    private void removeSessionCache(Session session) {
        ArrayList<String> keys = new ArrayList<>();
        session.getAttributeKeys().forEach(k->keys.add(k.toString()));
        keys.forEach(session::removeAttribute);
    }

    private void addDepartmentChoosingMenu(SendMessage message) {
        List<Department> departments = timetableService.findAllDepartments();
        List<String> departmentNames = new ArrayList<>(departments.size());
        for (Department department : departments) {
            departmentNames.add(department.getName());
        }
        message.setText(outputMessages.get(SELECT_DEPARTMENT));
        message.setReplyMarkup(menuMaker.makeStandardMenu(departmentNames, 1, false));
    }

    private InlineKeyboardMarkup getDayChoosingMenu(Session session) {
        Set<Day> daysWithLessons = new TreeSet<>();
        timetableService.findLessonByDepartmentAndGroup(session.getAttribute("department").toString(),
                session.getAttribute("group").toString()).forEach(l-> daysWithLessons.add(l.getDay()));
        List<String> listOfDayWithLessons = new ArrayList<>(daysWithLessons.size());
        for (Day day : daysWithLessons) {
            listOfDayWithLessons.add(day.getShortName());
        }
        return (InlineKeyboardMarkup)menuMaker.makeInlineMenu(listOfDayWithLessons, CALLBACK_PREFIX_DAY_CHOOSE + ":");
    }

    private void addGroupChoosingMenu(SendMessage message, String departmentName, Integer yearOfStudy) {
        List<Group> groups = timetableService.findGroupsByDepartmentNameAndYearOfStudy(departmentName, yearOfStudy);
        List<String> groupNames = new ArrayList<>(groups.size());
        for (Group group : groups) {
            groupNames.add(group.getName());
        }
        message.setReplyMarkup(menuMaker.makeStandardMenu(groupNames, 3, true));
    }

    private void addYearChoosingMenu(SendMessage message, String departmentName) {
        List<Group> groups = timetableService.findGroupsByDepartmentName(departmentName);
        TreeSet<String> years = new TreeSet<>();
        for (Group group : groups) {
            years.add(group.getYearOfStudy().toString());
        }
        message.setReplyMarkup(menuMaker.makeStandardMenu(new ArrayList<>(years), 6, true));
    }

    private void callbackDepartmentChoose(String departmentName, Long chatId, Session session) {
        session.setAttribute(DEPARTMENT_ATTRIBUTE, departmentName);
        session.setTimeout(Long.MAX_VALUE);
        SendMessage message = new SendMessage();
        message.setText(outputMessages.get(SELECTED) + " " + departmentName);
        addYearChoosingMenu(message, session.getAttribute(DEPARTMENT_ATTRIBUTE).toString());
        session.setAttribute(IS_GROUP_SHOWED_ATTRIBUTE, true);
        sendMessage(message, chatId);
    }

    private void callbackDayChoose(String dayName, Message input, Session session) {
        String fullDayName = Day.getByShortName(dayName).getFullName();
        if (!input.getText().contains(fullDayName)) {
            EditMessageText message = new EditMessageText();
            message.setReplyMarkup(getDayChoosingMenu(session));
            message.setMessageId(input.getMessageId());
            message.setChatId(input.getChatId());
            String departmentName = session.getAttribute(DEPARTMENT_ATTRIBUTE).toString();
            if (session.getAttribute("group") != null) {
                message.setParseMode("html");
                List<Lesson> lessons = timetableService
                        .findLessonByDepartmentAndGroupAndDay(departmentName, session.getAttribute("group").toString(), Day.getByShortName(dayName).toString());
                message.setText(outputFormatter.formatLessonsForADay(lessons, fullDayName));
            } else {
                message.setText(outputMessages.get(CHOICE_CANCELED));
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
