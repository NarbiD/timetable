package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.knu.timetable.model.*;

import java.util.Arrays;
import java.util.List;

@Service
public class TimetableService {

    final private DepartmentService departmentService;
    final private GroupService groupService;
    final private TeacherService teacherService;
    final private LessonService lessonService;

    @Autowired
    public TimetableService(LessonService lessonService, DepartmentService departmentService, GroupService groupService, TeacherService teacherService) {
        this.lessonService = lessonService;
        this.departmentService = departmentService;
        this.groupService = groupService;
        this.teacherService = teacherService;
    }

    public List<Department> findAllDepartments() {
        return departmentService.findAll();
    }

    public List<Day> findAllDays() {
        return Arrays.asList(Day.values());
    }

    public List<Lesson> findLessonByDepartmentAndGroup(String departmentName, String groupName) {
        return lessonService.findByDepartmentAndGroup(departmentName, groupName);
    }

    public List<Lesson> findLessonByDepartmentAndGroupAndDay(String departmentName, String groupName, String day) {
        return lessonService.findByDepartmentAndGroupAndDay(departmentName, groupName, day);
    }

    public List<Lesson> findLessonByDepartmentAndTeacherAndDay(String departmentName, String teacherName, String day) {
        return lessonService.findByDepartmentAndTeacherAndDay(departmentName, teacherName, day);
    }

    public List<Group> findGroupsByDepartment(String departmentName) {
        return groupService.findByDepartment(departmentName);
    }

    public List<Group> findGroupsByDepartmentAndYearOfStudy(String departmentName, Integer yearOfStudy) {
        return groupService.findByYearOfStudy(departmentName, yearOfStudy);
    }

    public List<Teacher> findTeacherByNameAndDepartment(String name, String departmentName) {
        return teacherService.findByNameAndDepartment(name, departmentName);
    }

    public List<Teacher> findTeacherByDepartment(String departmentName) {
        return teacherService.findByDepartment(departmentName);
    }
}
