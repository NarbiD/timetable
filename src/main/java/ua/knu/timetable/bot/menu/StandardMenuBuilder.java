package ua.knu.timetable.bot.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StandardMenuBuilder {
    private List<String> buttonNames;
    private List<String> fullSizeButtons;
    private int buttonPerLine;
    private boolean supportButtons;
    private static Properties defaultButtons;

    static {
        final String absolutePath = new File("").getAbsolutePath();
        final String pathToProperties = "/src/main/resources/";
        defaultButtons = new Properties();

        try (FileReader reader = new FileReader(new File(absolutePath + pathToProperties + "buttons.properties"))) {
            defaultButtons.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StandardMenuBuilder setButtonPerLine(int buttonPerLine) {
        this.buttonPerLine = buttonPerLine;
        return this;
    }

    public StandardMenuBuilder setSupportButtons(boolean supportButtons) {
        this.supportButtons = supportButtons;
        return this;
    }

    public StandardMenuBuilder setButtonNames(List<String> buttonNames) {
        this.buttonNames = buttonNames;
        return this;
    }

    public StandardMenuBuilder setFullSizeButtons(List<String> fullSizeButtons) {
        this.fullSizeButtons = fullSizeButtons;
        return this;
    }

    public ReplyKeyboardMarkup build() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = makeRows(this.buttonNames, buttonPerLine==0?1:buttonPerLine);
        keyboard.addAll(makeRows(fullSizeButtons, 1));

        if (supportButtons) {
            keyboard.add(supportButtons());
        }
        return replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private List<KeyboardRow> makeRows(List<String> buttonNames, int buttonPerLine) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        if (buttonNames != null) {
            for (int i = 0; i < buttonNames.size();) {
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(buttonNames.get(i++));
                while(i%buttonPerLine != 0 && i < buttonNames.size()) {
                    keyboardRow.add(buttonNames.get(i++));
                }
                keyboard.add(keyboardRow);
            }
        }
        return keyboard;
    }

    private KeyboardRow supportButtons() {
        KeyboardRow supportButtons = new KeyboardRow();
        supportButtons.add(defaultButtons.getProperty("ua.support_button"));
        supportButtons.add(defaultButtons.getProperty("ua.change_department_button"));
        return supportButtons;
    }
}