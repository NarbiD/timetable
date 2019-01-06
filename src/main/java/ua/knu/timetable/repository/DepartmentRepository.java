package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department getDepartmentByName(String name);
}
