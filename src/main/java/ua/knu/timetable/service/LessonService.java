package ua.knu.timetable.service;

import org.springframework.stereotype.Service;
import ua.knu.timetable.model.Day;
import ua.knu.timetable.model.Lesson;
import ua.knu.timetable.repository.LessonRepository;

import java.util.List;

@Service
public class LessonService {
    final private LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> findByDepartmentAndGroup(String departmentName, String groupName) {
        return lessonRepository.findLessonsByDepartmentNameAndGroupName(departmentName, groupName);
    }

    public List<Lesson> findByDepartmentAndGroupAndDay(String departmentName, String groupName, String day) {
        return lessonRepository.findLessonsByDepartmentNameAndGroupNameAndDay(departmentName, groupName, Day.valueOf(day));
    }

    public List<Lesson> findByDepartmentAndTeacherAndDay(String departmentName, String teacherName, String day) {
        return lessonRepository.findLessonsByDepartmentNameAndTeacherNameAndDay(departmentName, teacherName, Day.valueOf(day));
    }

}
