package ua.knu.timetable.bot;

import ua.knu.timetable.model.Lesson;

import java.util.*;

class OutputFormatter {
    String formatLessonsForADay(List<Lesson> lessons, String day) {
        StringBuilder timetable = new StringBuilder();
        if (!lessons.isEmpty()) {
            timetable.append("\u2705 <b>")
                    .append(day)
                    .append("</b>\n\n");
            lessons.sort(Comparator.comparingInt(l -> l.getClassTime().getLessonNumber()));
            List<String> emoj = Arrays.asList("\u0031\u20E3 ", "\u0032\u20E3 ", "\u0033\u20E3 ");
            Iterator<String> i = emoj.iterator();
            for (Lesson lesson : lessons) {
                timetable.append(i.next())
                        .append("<b>")
                        .append(lesson.getSubject().getName())
                        .append("</b> \n")
                        .append("<i>")
                        .append("Ауд. ").append(lesson.getAudience().getName()).append(", ")
                        .append(lesson.getTeacher().getName())
                        .append("</i> ").append("\n");
            }
        }
        return timetable.toString();
    }
}
