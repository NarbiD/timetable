package ua.knu.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Teacher> teachers;

    @OneToMany(mappedBy = "department")
    private List<Audience> audiences;

//    @OneToMany(mappedBy = "department")
//    private List<Group> groups;
//    @OneToMany(mappedBy = "department")
//    private List<Lesson> lessons;
    @OneToMany(mappedBy = "department")
    private List<Subject> subjects;
//
}
