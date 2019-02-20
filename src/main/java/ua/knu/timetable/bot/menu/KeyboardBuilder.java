package ua.knu.timetable.bot.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

class KeyboardBuilder {
    private List<String> buttonNames;
    private List<String> fullSizeButtons;
    private String icon;
    private int buttonPerLine;
    private boolean autoResize;
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

    KeyboardBuilder() {
        buttonNames = new ArrayList<>();
        fullSizeButtons = new ArrayList<>();
    }

    KeyboardBuilder setButtonPerLine(int buttonPerLine) {
        this.buttonPerLine = buttonPerLine;
        return this;
    }

    public KeyboardBuilder setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
        return this;
    }

    KeyboardBuilder setSupportButtons(boolean supportButtons) {
        this.supportButtons = supportButtons;
        return this;
    }

    KeyboardBuilder setButtonNames(Collection<String> buttonNames) {
        this.buttonNames.addAll(buttonNames);
        return this;
    }

    KeyboardBuilder setFullSizeButtons(Collection<String> fullSizeButtons) {
        this.fullSizeButtons.addAll(fullSizeButtons);
        return this;
    }

    KeyboardBuilder setIcon(String icon) {
        this.icon = icon;
        return this;
    }


    ReplyKeyboardMarkup build() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        final String tag = this.icon!=null ? this.icon+"  " : "";
        if (this.buttonNames != null) {
            buttonNames = buttonNames.stream().map(name->tag+name).collect(Collectors.toList());
            buttonPerLine = autoResize ? dividerSelection(buttonNames.size()) : buttonPerLine;
            keyboard.addAll(makeRows(buttonNames, buttonPerLine, true));
        }
        if (this.fullSizeButtons != null) {
            fullSizeButtons = fullSizeButtons.stream().map(name->tag+name).collect(Collectors.toList());
            keyboard.addAll(makeRows(fullSizeButtons, 1, false));
        }
        if (this.supportButtons) {
            keyboard.add(supportButtons());
        }
        return replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private int dividerSelection(int numberOfButtons) {
        if (numberOfButtons%4 == 0 || (numberOfButtons+1)%4 == 0) {
            return 4;
        } else {
            return 3;
        }
    }

    private static List<KeyboardRow> makeRows(List<String> buttonNames, int buttonPerLine, boolean joinLastButton) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        if (buttonNames != null) {
            int keyboardSize = buttonNames.size();
            for (int i = 0; i < keyboardSize;) {
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(buttonNames.get(i++));

                boolean lastButtonCondition = joinLastButton && i == (keyboardSize - 1);
                while((i%buttonPerLine != 0 || lastButtonCondition) && i < keyboardSize) {
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