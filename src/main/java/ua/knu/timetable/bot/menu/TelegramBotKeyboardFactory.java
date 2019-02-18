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
    public ReplyKeyboard makeResizableKeyboard(Collection<String> buttons) {
        return makeResizableKeyboard(buttons, "");
    }

    @Override
    public ReplyKeyboard makeResizableKeyboard(Collection<String> buttons, String icon) {
        return new KeyboardBuilder()
                .setAutoResize(true)
                .setIcon(icon)
                .setSupportButtons(true)
                .setButtonNames(buttons)
                .build();
    }

    @Override
    public ReplyKeyboard makeKeyboard(List<String> buttonNames, String icon) {
        return makeKeyboard(buttonNames, icon, false);
    }

    @Override
    public ReplyKeyboard makeKeyboard(List<String> buttonNames, String icon, boolean includeSupportButtons) {
        return new KeyboardBuilder()
                .setFullSizeButtons(buttonNames)
                .setSupportButtons(includeSupportButtons)
                .setIcon(icon)
                .build();
    }
}
