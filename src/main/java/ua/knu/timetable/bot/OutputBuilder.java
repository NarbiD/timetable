package ua.knu.timetable.bot;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.knu.timetable.model.Group;
import ua.knu.timetable.model.Lesson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static ua.knu.timetable.bot.OutputBuilder.OutputText.*;

class OutputBuilder {

    private static Properties textProps;

    static {
        final String absolutePath = new File("").getAbsolutePath();
        final String pathToProperties = "/src/main/resources/";

        try (FileReader textPropertiesReader = new FileReader(
                     new File(absolutePath + pathToProperties + "lang.properties"))){
            textProps = new Properties();
            textProps.load(textPropertiesReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    enum OutputBit {
        SUBJECT, AUDIENCE, TEACHER, GROUP, SUBGROUP
    }

    @AllArgsConstructor
    @Getter
    enum OutputText {
        COMMENT(textProps.getProperty(LangCode.UA.getCode()+".comment")),
        EVEN(textProps.getProperty(LangCode.UA.getCode()+".even")),
        ODD(textProps.getProperty(LangCode.UA.getCode()+".odd")),
        SUBGROUP(textProps.getProperty(LangCode.UA.getCode()+".subgroup")),
        GROUP(textProps.getProperty(LangCode.UA.getCode()+".group")),
        AUDIENCE(textProps.getProperty(LangCode.UA.getCode()+".audience")),
        NOW(textProps.getProperty(LangCode.UA.getCode()+".now"));

        private String text;
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
                String subgroup = lesson.getSubgroup().equals("0") ? "" : SUBGROUP.getText()+" "+lesson.getSubgroup();
                String week = lesson.getWeek().equals("0") ? "" :
                        lesson.getWeek().equals("2") ? ODD.getText() : EVEN.getText();

                String numberEmoji = EMOJI_NUMBERS[lesson.getClassTime().getLessonNumber()] + " ";
                numberEmoji = (timetable.toString().contains(numberEmoji))?"      ":numberEmoji;
                timetable.append("\n").append(numberEmoji);

                if (this.includes.get(OutputBit.SUBJECT)) {
                    timetable.append("<b>").append(lesson.getSubject().getName())
                            .append(" (").append(lesson.getFormat()).append(")")
                            .append("</b>");
                }
                timetable.append(week);
                if (this.includes.get(OutputBit.AUDIENCE)) {
                    timetable.append("\n<i>")
                            .append(AUDIENCE.getText()).append(" ")
                            .append(lesson.getAudience().getName())
                            .append("</i>");
                }
                if (this.includes.get(OutputBit.TEACHER)) {
                    timetable.append("<i>").append(" | ")
                            .append(lesson.getTeacher().getName())
                            .append("</i>");
                }
                if (this.includes.get(OutputBit.GROUP)) {
                    timetable.append("<i>").append(" | ")
                            .append(GROUP.getText()).append(" ")
                            .append(lesson.getGroup().getName())
                            .append("</i>");
                }
                if (this.includes.get(OutputBit.SUBGROUP) && !subgroup.equals("")) {
                    timetable.append("<i>").append(" | ")
                            .append(subgroup)
                            .append("</i>");
                }
            }
            if (timetable.toString().contains(EVEN.getText()) || timetable.toString().contains(ODD.getText())) {
                this.text = NOW.getText() + " " +
                        (WeekParityChecker.INSTANCE.checkParity(new Date()) ? ODD.getText() : EVEN.getText());
            }
        }
        if (text!=null && !text.equals("")) {
            timetable.append("\n\n<i>")
                    .append(COMMENT.getText()).append(": ")
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
