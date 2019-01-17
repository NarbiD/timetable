package ua.knu.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group getGroupByDepartmentAndName(Department department, String name);
    List<Group> findAllByDepartment_NameOrderByName(String departmentName);
    List<Group> findAllByDepartment_NameAndYearOfStudyOrderByName(String departmentName, Integer year);
}
