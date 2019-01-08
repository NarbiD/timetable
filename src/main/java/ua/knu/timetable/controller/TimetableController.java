package ua.knu.timetable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.timetable.dto.LessonDto;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.model.Lesson;
import ua.knu.timetable.service.DepartmentService;
import ua.knu.timetable.service.GroupService;
import ua.knu.timetable.service.TimetableService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/timetable")
public class TimetableController {
    final private TimetableService timetableService;

    @Autowired
    public TimetableController(TimetableService timetableService, DepartmentService departmentService, GroupService groupService) {
        this.timetableService = timetableService;
    }

    @RequestMapping(value = "/{departmentName}/{groupName}", method = RequestMethod.GET)
    public List<LessonDto> getLessonsByDepartmentAndGroup(@PathVariable String departmentName, @PathVariable String groupName) {
        List<LessonDto> lessonDtos = new ArrayList<>();
        List<Lesson> lessons = timetableService.findLessonByDepartmentAndGroup(departmentName, groupName);
        for (Lesson lesson : lessons) {
            lessonDtos.add(new LessonDto(lesson));
        }
        return lessonDtos;
    }
}

