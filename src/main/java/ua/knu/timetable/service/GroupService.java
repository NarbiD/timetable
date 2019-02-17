package ua.knu.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.repository.GroupRepository;

import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group getGroupByNameAndDepartmentName(String name, String departmentName) {
        return groupRepository.findByNameAndDepartment_Name(name, departmentName);
    }

    public List<Group> findAllByYearOfStudy(String departmentName, Integer yearOfStudy) {
        return groupRepository.findAllByDepartment_NameAndYearOfStudyOrderByName(departmentName, yearOfStudy);
    }

    public List<Group> findAllByDepartmentName(String departmentName) {
        return groupRepository.findAllByDepartment_NameOrderByName(departmentName);
    }


}
