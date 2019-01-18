package ua.knu.timetable.bot.menu;

public interface MenuFactory<T> {
    T makeDepartmentSelectionMenu();

    T makeDaySelectionMenu(String departmentName, String groupName, String callbackPrefix);

    T makeGroupSelectionMenu(String departmentName, Integer yearOfStudy);

    T makeYearSelectionMenu(String departmentName);
}
