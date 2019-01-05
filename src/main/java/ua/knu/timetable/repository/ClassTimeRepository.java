package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.ClassTime;

public interface ClassTimeRepository extends JpaRepository<ClassTime, Long> {
}
