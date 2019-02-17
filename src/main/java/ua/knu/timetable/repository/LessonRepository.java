package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.*;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findLessonsByDepartmentAndGroup(Department department, Group group);
    List<Lesson> findLessonsByDepartmentAndGroupAndDay(Department department, Group group, Day day);
    List<Lesson> findLessonsByDepartmentAndTeacher(Department department, Teacher teacher);
    List<Lesson> findLessonsByDepartmentAndTeacherAndDay(Department department, Teacher teacher, Day day);

}
