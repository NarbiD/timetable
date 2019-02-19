package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.*;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findLessonsByDepartmentNameAndGroupName(String department, String group);
    List<Lesson> findLessonsByDepartmentNameAndGroupNameAndDay(String department, String group, Day day);
    List<Lesson> findLessonsByDepartmentNameAndTeacherName(String department, String teacher);
    List<Lesson> findLessonsByDepartmentNameAndTeacherNameAndDay(String department, String teacher, Day day);

}
