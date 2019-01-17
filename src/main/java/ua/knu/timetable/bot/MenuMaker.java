package ua.knu.timetable.bot;

import java.util.List;

public interface MenuMaker<T> {
    T makeInlineMenu(List<String> buttonNames, String callbackPrefix);
    T makeStandartMenu(List<String> buttonNames);
}
