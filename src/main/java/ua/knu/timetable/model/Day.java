package ua.knu.timetable.model;

public enum Day {
    MONDAY("Понеділок", "Пн"),
    TUESDAY("Вівторок", "Вт"),
    WEDNESDAY("Середа", "Ср"),
    THURSDAY("Четвер", "Чт"),
    FRIDAY("П'ятниця", "Пт");

    private String fullName;
    private String shortName;

    Day(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getShortName() {
        return this.shortName;
    }

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
        throw new IllegalArgumentException("Incorrect day");
    }
}
