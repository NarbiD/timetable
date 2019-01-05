package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
