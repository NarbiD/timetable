package ua.knu.timetable.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Day day;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "audience_id")
    private Audience audience;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "class_time_id")
    private ClassTime classTime;

    private String week;
    private String subgroup;
    private String format;

    public boolean equalsExcludingFormatAndWeek(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return  day == lesson.day &&
                Objects.equals(department, lesson.department) &&
                Objects.equals(subject, lesson.subject) &&
                Objects.equals(audience, lesson.audience) &&
                Objects.equals(teacher, lesson.teacher) &&
                Objects.equals(group, lesson.group) &&
                Objects.equals(classTime, lesson.classTime) &&
                Objects.equals(subgroup, lesson.subgroup);
    }

    public boolean equalsExcludingGroup(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return day == lesson.day &&
                Objects.equals(department, lesson.department) &&
                Objects.equals(subject, lesson.subject) &&
                Objects.equals(audience, lesson.audience) &&
                Objects.equals(teacher, lesson.teacher) &&
                Objects.equals(classTime, lesson.classTime) &&
                Objects.equals(week, lesson.week) &&
                Objects.equals(format, lesson.format);
    }

}
