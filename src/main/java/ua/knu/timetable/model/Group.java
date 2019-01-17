package ua.knu.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stud_group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer yearOfStudy;

    public String getName() {
        return name;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public Group(String name, Department department) {
        this.name = name;
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

}
