package ua.knu.timetable.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TelegramBotMenuMaker implements MenuMaker<ReplyKeyboard> {
    @Override
    public InlineKeyboardMarkup makeInlineMenu(List<String> buttonNames, String callbackPrefix) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (String name : buttonNames) {
            buttons.add(Collections.singletonList(new InlineKeyboardButton()
                    .setText(name)
                    .setCallbackData(callbackPrefix + name)));
        }
        return new InlineKeyboardMarkup().setKeyboard(buttons);
    }

    @Override
    public ReplyKeyboardMarkup makeStandartMenu(List<String> buttonNames) {
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
}
