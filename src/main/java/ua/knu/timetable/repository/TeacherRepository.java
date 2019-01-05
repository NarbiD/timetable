package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
