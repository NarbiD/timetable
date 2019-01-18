package ua.knu.timetable.bot.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.knu.timetable.model.Day;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.service.TimetableService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TelegramBotMenuFactory implements MenuFactory<ReplyKeyboard> {

    private TimetableService timetableService;

    public TelegramBotMenuFactory(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @Override
    public ReplyKeyboard makeDepartmentSelectionMenu() {
        List<Department> departments = timetableService.findAllDepartments();
        List<String> departmentNames = new ArrayList<>(departments.size());
        for (Department department : departments) {
            departmentNames.add(department.getName());
        }
        return new StandardMenuBuilder()
                .setButtonPerLine(1)
                .setButtonNames(departmentNames)
                .build();
    }

    @Override
    public ReplyKeyboard makeDaySelectionMenu(String departmentName, String groupName, String callbackPrefix) {
        Set<Day> daysWithLessons = new TreeSet<>();
        timetableService.findLessonByDepartmentAndGroup(departmentName, groupName)
                .forEach(l-> daysWithLessons.add(l.getDay()));
        List<String> listOfDayWithLessons = new ArrayList<>(daysWithLessons.size());
        for (Day day : daysWithLessons) {
            listOfDayWithLessons.add(day.getShortName());
        }
        return new InlineMenuBuilder()
                .setCallbackPrefix(callbackPrefix + ":")
                .setButtonNames(listOfDayWithLessons)
                .build();
    }

    @Override
    public ReplyKeyboard makeGroupSelectionMenu(String departmentName, Integer yearOfStudy) {
        List<Group> groups = timetableService.findGroupsByDepartmentNameAndYearOfStudy(departmentName, yearOfStudy);
        List<String> groupNames = new ArrayList<>(groups.size());
        for (Group group : groups) {
            groupNames.add(group.getName());
        }
        return  new StandardMenuBuilder()
                .setButtonPerLine(4)
                .setSupportButtons(true)
                .setButtonNames(groupNames)
                .build();
    }

    @Override
    public ReplyKeyboard makeYearSelectionMenu(String departmentName) {
        List<Group> groups = timetableService.findGroupsByDepartmentName(departmentName);
        TreeSet<String> years = new TreeSet<>();
        for (Group group : groups) {
            years.add(group.getYearOfStudy().toString());
        }
        return new StandardMenuBuilder()
                .setButtonPerLine(6)
                .setSupportButtons(true)
                .setButtonNames(new ArrayList<>(years))
                .build();
    }
}
