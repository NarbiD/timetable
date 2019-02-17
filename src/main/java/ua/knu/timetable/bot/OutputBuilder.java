package ua.knu.timetable.bot;

import com.vdurmont.emoji.EmojiParser;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.model.Lesson;

import java.util.*;

public class OutputBuilder {

    enum OutputBit {
        SUBJECT, AUDIENCE, TEACHER, GROUP, SUBGROUP
    }

    private final static String[] EMOJI_NUMBERS = {":zero:", ":one:", ":two:", ":three:", ":four:",
                            ":five:", ":six:", ":seven:", ":eight:", ":nine:"};

    private String day;
    private String text;
    private List<Lesson> lessons;
    private Map<OutputBit, Boolean> includes = new EnumMap<OutputBit, Boolean>(OutputBit.class) {{
        for (OutputBit value : OutputBit.values()) {
            put(value, false);
        }
    }};

    OutputBuilder setDay(String day) {
        this.day = day;
        return this;
    }

    OutputBuilder setText(String text) {
        this.text = text;
        return this;
    }

    OutputBuilder setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        return this;
    }

    OutputBuilder includeSubject(boolean isInclude) {
        this.includes.put(OutputBit.SUBJECT, isInclude);
        return this;
    }

    OutputBuilder includeAudience(boolean isInclude) {
        this.includes.put(OutputBit.AUDIENCE, isInclude);
        return this;
    }

    OutputBuilder includeTeacher(boolean isInclude) {
        this.includes.put(OutputBit.TEACHER, isInclude);
        return this;
    }

    OutputBuilder includeGroup(boolean isInclude) {
        this.includes.put(OutputBit.GROUP, isInclude);
        return this;
    }

    OutputBuilder includeSubgroup(boolean isInclude) {
        this.includes.put(OutputBit.SUBGROUP, isInclude);
        return this;
    }

    String build() {
        StringBuilder timetable = new StringBuilder();

        if (day!=null) {
            timetable.append(":large_blue_diamond:  ")
                    .append("<b>")
                    .append(day)
                    .append("</b>\n");
        }

        if (lessons != null && !lessons.isEmpty()) {
            lessons.sort(Comparator.<Lesson>comparingInt(l -> l.getClassTime().getLessonNumber())
                    .thenComparing(Lesson::getWeek)
                    .thenComparing(Lesson::getSubgroup));
            removeDuplicates(lessons);

            for (Lesson lesson : lessons) {
                String subgroup = lesson.getSubgroup().equals("0") ? "" : "(підгрупа "+lesson.getSubgroup()+")";
                String week = lesson.getWeek().equals("0") ? "" :
                        lesson.getWeek().equals("2") ? "\n<i>(парний тиждень)</i>" : "\n<i>(непарний тиждень)</i>";

                String numberEmoji = EMOJI_NUMBERS[lesson.getClassTime().getLessonNumber()] + " ";
                numberEmoji = (timetable.toString().contains(numberEmoji))?"      ":numberEmoji;
                timetable.append("\n").append(numberEmoji);

                if (this.includes.get(OutputBit.SUBJECT)) {
                    timetable.append("<b>").append(lesson.getSubject().getName())
                            .append(" (").append(lesson.getFormat()).append(")")
                            .append("</b>");
                }
                if (this.includes.get(OutputBit.GROUP)) {
                    timetable.append("\n")
                            .append("Група ")
                            .append(lesson.getGroup().getName());
                }
                if (this.includes.get(OutputBit.SUBGROUP) && !subgroup.equals("")) {
                    timetable.append(this.includes.get(OutputBit.GROUP) ? " " : "\n")
                            .append(subgroup);
                }
                timetable.append(week);
                if (this.includes.get(OutputBit.AUDIENCE)) {
                    timetable.append("\n<i>")
                            .append("Ауд. ").append(lesson.getAudience().getName())
                            .append("</i>");
                }
                if (this.includes.get(OutputBit.TEACHER)) {
                    timetable.append("<i>").append(" | ")
                            .append(lesson.getTeacher().getName())
                            .append("</i>");
                }
            }
            if (timetable.toString().contains("парний тиждень")) {
                this.text = "Зараз " + (WeekParityChecker.INSTANCE.checkParity(new Date()) ? "" : "не") + "парний тиждень";
            }
        }
        if (text!=null && !text.equals("")) {
            timetable.append("\n\n<i>Примітка: ")
                    .append(text)
                    .append("</i>");
        }

        return EmojiParser.parseToUnicode(timetable.toString());
    }

    private void removeDuplicates(List<Lesson> lessons) {
        List<Lesson> duplicates = new ArrayList<>();
        for (int i = 1; i < lessons.size(); i++) {
            if (lessons.get(i).equalsExcludingFormatAndWeek(lessons.get(i-1))) {
                lessons.get((i-1)).setFormat(lessons.get(i-1).getFormat() + "+" + lessons.get(i).getFormat());
                duplicates.add(lessons.get(i));
            }
            if (lessons.get(i).equalsExcludingGroup(lessons.get(i-1))) {
                Group group = new Group();
                group.setName(lessons.get(i-1).getGroup().getName() + ", " + lessons.get(i).getGroup().getName());
                lessons.get(i).setGroup(group);
                duplicates.add(lessons.get(i-1));
            }
        }
        lessons.removeAll(duplicates);
    }
}
