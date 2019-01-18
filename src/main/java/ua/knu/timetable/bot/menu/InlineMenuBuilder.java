package ua.knu.timetable.bot.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InlineMenuBuilder {
    private List<String> buttonNames;
    private List<String> fullSizeButtons;
    private String callbackPrefix;

    public InlineMenuBuilder setCallbackPrefix(String callbackPrefix) {
        this.callbackPrefix = callbackPrefix;
        return this;
    }

    public InlineMenuBuilder setButtonNames(List<String> buttonNames) {
        this.buttonNames = buttonNames;
        return this;
    }

    public InlineMenuBuilder setFullSizeButtons(List<String> fullSizeButtons) {
        this.fullSizeButtons = fullSizeButtons;
        return this;
    }

    public InlineKeyboardMarkup build() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        if (buttonNames != null) {
            for (String name : buttonNames) {
                buttons.add(new InlineKeyboardButton()
                        .setText(name)
                        .setCallbackData(callbackPrefix + name));
            }
        }
        keyboard.add(buttons);
        if (fullSizeButtons != null) {
            for (String name : fullSizeButtons) {
                keyboard.add(Collections.singletonList(new InlineKeyboardButton(name)));
            }
        }
        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }
}