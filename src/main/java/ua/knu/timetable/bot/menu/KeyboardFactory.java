package ua.knu.timetable.bot.menu;

import java.util.Collection;
import java.util.List;

public interface KeyboardFactory<T> {
    T makeInlineKeyboard(Collection<String> buttons, String callbackPrefix);

    T makeResizableKeyboard(Collection<String> buttons, int size);

    T makeResizableKeyboard(Collection<String> buttons, String icon, int size);

    T makeKeyboard(List<String> buttonNames, String icon);

}
