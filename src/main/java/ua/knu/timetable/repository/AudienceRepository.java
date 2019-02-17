package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Audience;
import ua.knu.timetable.model.Department;

public interface AudienceRepository extends JpaRepository<Audience, Long> {
    Audience findByNameAndDepartment_Name(String name, String departmentName);
}
