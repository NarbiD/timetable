package ua.knu.timetable.dto;

import lombok.Data;
import ua.knu.timetable.model.ClassTime;
import ua.knu.timetable.model.Lesson;

@Data
public class LessonDto {
    private String day;
    private String department;
    private String subject;
    private String audience;
    private String teacher;
    private String group;
    private String startTime;
    private String endTime;
    private int lessonNumber;

    public LessonDto(Lesson lesson) {

        ClassTime classTime = lesson.getClassTime();
        this.day = lesson.getDay().name();
        this.department = lesson.getDepartment().getName();
        this.subject = lesson.getSubject().getName();
        this.audience = lesson.getAudience().getName();
        this.teacher = lesson.getTeacher().getName();
        this.group = lesson.getGroup().getName();
        this.startTime = classTime.getStartTime();
        this.endTime = classTime.getEndTime();
        this.lessonNumber = classTime.getLessonNumber();
    }
}
