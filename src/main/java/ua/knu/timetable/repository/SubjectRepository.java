package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
