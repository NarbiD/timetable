package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.model.Lesson;
import ua.knu.timetable.repository.LessonRepository;

import java.util.List;

@Service
public class TimetableService {

    final private LessonRepository lessonRepository;

    @Autowired
    public TimetableService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> findLessonByDepartmentAndGroup(Department department, Group group) {
        return lessonRepository.findLessonsByDepartmentAndGroup(department, group);
    }

}
