package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
