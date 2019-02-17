package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.knu.timetable.model.*;
import ua.knu.timetable.repository.LessonRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class TimetableService {

    final private LessonRepository lessonRepository;
    final private DepartmentService departmentService;
    final private GroupService groupService;
    final private TeacherService teacherService;

    @Autowired
    public TimetableService(LessonRepository lessonRepository, DepartmentService departmentService, GroupService groupService, TeacherService teacherService) {
        this.lessonRepository = lessonRepository;
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
        Department department = departmentService.getDepartmentByName(departmentName);
        Group group = groupService.getGroupByNameAndDepartmentName(groupName, departmentName);
        return lessonRepository.findLessonsByDepartmentAndGroup(department, group);
    }

    public List<Lesson> findLessonByDepartmentAndGroupAndDay(String departmentName, String groupName, String day) {
        Department department = departmentService.getDepartmentByName(departmentName);
        Group group = groupService.getGroupByNameAndDepartmentName(groupName, departmentName);
        return lessonRepository.findLessonsByDepartmentAndGroupAndDay(department, group, Day.valueOf(day));
    }

    public List<Lesson> findLessonByDepartmentAndTeacherAndDay(String departmentName, String teacherName, String day) {
        Department department = departmentService.getDepartmentByName(departmentName);
        Teacher teacher = teacherService.findTeacherByNameAndDepartmentName(teacherName, departmentName).get(0); // TODO: fix it
        return lessonRepository.findLessonsByDepartmentAndTeacherAndDay(department, teacher, Day.valueOf(day));
    }

    public List<Group> findGroupsByDepartmentName(String departmentName) {
        return groupService.findAllByDepartmentName(departmentName);
    }

    public List<Group> findGroupsByDepartmentNameAndYearOfStudy(String departmentName, Integer yearOfStudy) {
        return groupService.findAllByYearOfStudy(departmentName, yearOfStudy);
    }

    public List<Teacher> findTeacherByNameAndDepartmentName(String name, String departmentName) {
        return teacherService.findTeacherByNameAndDepartmentName(name, departmentName);
    }

    public List<Teacher> findTeacherByDepartmentName(String departmentName) {
        return teacherService.findByDepartmentName(departmentName);
    }
}
