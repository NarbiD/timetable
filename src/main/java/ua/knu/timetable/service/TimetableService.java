package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.knu.timetable.model.Day;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.model.Lesson;
import ua.knu.timetable.repository.LessonRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class TimetableService {

    final private LessonRepository lessonRepository;
    final private DepartmentService departmentService;
    final private GroupService groupService;

    @Autowired
    public TimetableService(LessonRepository lessonRepository, DepartmentService departmentService, GroupService groupService) {
        this.lessonRepository = lessonRepository;
        this.departmentService = departmentService;
        this.groupService = groupService;
    }

    public List<Department> findAllDepartments() {
        return departmentService.findAll();
    }

    public List<Day> findAllDays() {
        return Arrays.asList(Day.values());
    }

    public List<Lesson> findLessonByDepartmentAndGroup(String departmentName, String groupName) {
        Department department = departmentService.getDepartmentByName(departmentName);
        Group group = groupService.getGroupByDepartmentAndName(department, groupName);
        return lessonRepository.findLessonsByDepartmentAndGroup(department, group);
    }

    public List<Lesson> findLessonByDepartmentAndGroupAndDay(String departmentName, String groupName, String day) {
        Department department = departmentService.getDepartmentByName(departmentName);
        Group group = groupService.getGroupByDepartmentAndName(department, groupName);
        return lessonRepository.findLessonsByDepartmentAndGroupAndDay(department, group, Day.valueOf(day));
    }

    public List<Group> findGroupsByDepartmentName(String departmentName) {
        return groupService.findAllByDepartmentName(departmentName);
    }

    public List<Group> findGroupsByDepartmentNameAndYearOfStudy(String departmentName, Integer yearOfStudy) {
        return groupService.findAllByYearOfStudy(departmentName, yearOfStudy);
    }
}
