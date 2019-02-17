package ua.knu.timetable.bot.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.*;


public class TelegramBotKeyboardFactory implements KeyboardFactory<ReplyKeyboard> {

    @Override
    public ReplyKeyboard makeInlineKeyboard(Collection<String> buttons, String callbackPrefix) {
        return new InlineKeyboardBuilder()
                .setCallbackPrefix(callbackPrefix + ":")
                .setButtonNames(buttons)
                .build();
    }

    @Override
    public ReplyKeyboard makeResizableKeyboard(Collection<String> buttons, int size) {
        return makeResizableKeyboard(buttons, "", size);
    }

    @Override
    public ReplyKeyboard makeResizableKeyboard(Collection<String> buttons, String icon, int size) {
        return new KeyboardBuilder()
                .setButtonPerLine(size)
                .setIcon(icon)
                .setSupportButtons(true)
                .setButtonNames(buttons)
                .build();
    }

    @Override
    public ReplyKeyboard makeKeyboard(List<String> buttonNames, String icon) {
        return new KeyboardBuilder()
                .setFullSizeButtons(buttonNames)
                .setIcon(icon)
                .setSupportButtons(true)
                .build();
    }
}
