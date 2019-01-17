package ua.knu.timetable.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TelegramBotMenuMaker implements MenuMaker<ReplyKeyboard> {

    @Override
    public InlineKeyboardMarkup makeInlineMenu(List<String> buttonNames, String callbackPrefix) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (String name : buttonNames) {
            buttons.add(new InlineKeyboardButton()
                    .setText(name)
                    .setCallbackData(callbackPrefix + name));
        }
        return new InlineKeyboardMarkup().setKeyboard(Collections.singletonList(buttons));
    }

    @Override
    public ReplyKeyboardMarkup makeStandardMenu(List<String> buttonNames, int buttonPerLine, boolean setSupportButtons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (int i = 0; i < buttonNames.size();) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(buttonNames.get(i++));
            while(i%buttonPerLine != 0 && i < buttonNames.size()) {
                keyboardRow.add(buttonNames.get(i++));
            }
            keyboard.add(keyboardRow);
        }
        if (setSupportButtons) {
            keyboard.add(supportButtons());
        }
        return replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private KeyboardRow supportButtons() {
        KeyboardRow supportButtons = new KeyboardRow();
        supportButtons.add("\u2139 Підтримка");
        supportButtons.add("\u2B05 Інший факультет");
        return supportButtons;
    }

}
