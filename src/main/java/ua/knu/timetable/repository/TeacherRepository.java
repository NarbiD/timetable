package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.knu.timetable.model.Teacher;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher>  findByDepartment_Name(String departmentName);
    Teacher findByNameAndDepartment_Name(String name, String departmentName);
    List<Teacher> findByNameContainingAndDepartment_Name(String name, String departmentName);
}
