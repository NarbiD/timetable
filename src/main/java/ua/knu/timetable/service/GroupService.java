package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.knu.timetable.model.Department;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.repository.GroupRepository;

import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    final private DepartmentService departmentService;

    @Autowired
    public GroupService(GroupRepository groupRepository, DepartmentService departmentService) {
        this.groupRepository = groupRepository;
        this.departmentService = departmentService;
    }

    public Group getGroupByDepartmentAndName(Department department, String name) {
        return groupRepository.getGroupByDepartmentAndName(department, name);
    }

    public List<Group> findAllByDepartmentName(String departmentName) {
        return groupRepository.findAllByDepartment_Name(departmentName);
    }


}
