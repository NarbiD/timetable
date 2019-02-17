package ua.knu.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Day {
    MONDAY("Понеділок", "Пн"),
    TUESDAY("Вівторок", "Вт"),
    WEDNESDAY("Середа", "Ср"),
    THURSDAY("Четвер", "Чт"),
    FRIDAY("П'ятниця", "Пт");

    private String visibleName;
    private String shortName;

    public static Day getByShortName(String day) {
        switch (day) {
            case "Пн":
                return MONDAY;
            case "Вт":
                return TUESDAY;
            case "Ср":
                return WEDNESDAY;
            case "Чт":
                return THURSDAY;
            case "Пт":
                return FRIDAY;
        }
        throw new IllegalArgumentException("Incorrect day: " + day);
    }
}
