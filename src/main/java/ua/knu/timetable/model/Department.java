package ua.knu.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Teacher> teachers;
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Audience> audiences;
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Group> groups;
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Subject> subjects;

}
