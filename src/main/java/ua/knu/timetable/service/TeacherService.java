package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.knu.timetable.model.Teacher;
import ua.knu.timetable.repository.TeacherRepository;

import java.util.List;
@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    List<Teacher> findTeacherByNameAndDepartmentName(String name, String departmentName) {
        return teacherRepository.findByNameContainingAndDepartment_Name(name, departmentName);
    }

    List<Teacher> findByDepartmentName(String departmentName) {
        return teacherRepository.findByDepartment_Name(departmentName);
    }
}
