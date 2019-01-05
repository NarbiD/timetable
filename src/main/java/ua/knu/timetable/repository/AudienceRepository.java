package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Audience;

public interface AudienceRepository extends JpaRepository<Audience, Long> {
}
