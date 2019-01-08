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
    private final String DEPARTMENT_ATTRIBUTE = "department";
    private final String IS_GROUP_SHOWED_ATTRIBUTE = "isGroupMenuShowed";
    private final String CALLBACK_PREFIX_DEPARTMENT_CHOOSE = "departmentChoose:";

    private Properties properties;
    private TimetableService timetableService;

    public TelegramBot(TimetableService timetableService) throws IOException {
        super();
        this.timetableService = timetableService;
        loadProperties();
    }

    private void loadProperties() throws IOException {
        final String absolutePath = new File("").getAbsolutePath();
        final String pathToProperties = "/src/main/resources/telegramBot.properties";
        properties = new Properties();
        properties.load(new FileReader(new File(absolutePath + pathToProperties)));
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
                        if (s.getAttribute(DEPARTMENT_ATTRIBUTE) != null && session.get().getAttribute(IS_GROUP_SHOWED_ATTRIBUTE) != null) {
                            //TODO show available days
                        }
                    });
            sendMessage(message, update.getMessage().getChatId());
        } else if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            String[] queryAndAnswer = callbackQuery.split(":");
            String req = queryAndAnswer[0];
            String resp = queryAndAnswer[1];
            switch (req) {
                case CALLBACK_PREFIX_DEPARTMENT_CHOOSE:
                    session.ifPresent(s ->
                            callbackDepartmentChoose(resp, update.getCallbackQuery().getMessage().getChatId(), s));
                    break;
            }
        }
    }

    private void start(Session session, Long chatId) {
        removeSessionCache(session);
        SendMessage message = new SendMessage();
        String WELCOME = "Вас вітає бот, що допоможе вам знайти розклад.";
        String CHOOSES_CANCELED = "Вибір скасовано.";
        String outputText = session.getAttribute(DEPARTMENT_ATTRIBUTE) == null ? WELCOME : CHOOSES_CANCELED;
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
        session.getAttributeKeys().forEach(session::removeAttribute);
    }

    private void addDepartmentChoosingMenu(SendMessage message) {
        List<Department> departments = timetableService.findAllDepartments();
        List<String> departmentNames = new ArrayList<>(departments.size());
        for (Department department : departments) {
            departmentNames.add(department.getName());
        }
        message.setText("Оберіть факультет:");
        message.setReplyMarkup(makeInlineKeyboard(departmentNames, CALLBACK_PREFIX_DEPARTMENT_CHOOSE));
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

    private void callbackDepartmentChoose(String departmentName, Long chatId, Session session) {
        session.setAttribute(DEPARTMENT_ATTRIBUTE, departmentName);
        SendMessage message = new SendMessage();
        message.setText("Обрано " + departmentName +
                ".\n Тепер оберіть групу.");
        addGroupChoosingMenu(message, session.getAttribute(DEPARTMENT_ATTRIBUTE).toString());
        session.setAttribute(IS_GROUP_SHOWED_ATTRIBUTE, true);
        sendMessage(message, chatId);
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
