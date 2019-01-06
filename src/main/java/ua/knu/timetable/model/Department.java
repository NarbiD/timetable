package ua.knu.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getName() {
        return name;
    }

    private String name;

    public Department(String name) {
        this.name = name;
    }

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
