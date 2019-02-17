package ua.knu.timetable.bot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum WeekParityChecker {
    INSTANCE;

    private Date firstDay;

    public void setFirstDay(String startSemester) {
        try {
            firstDay = new SimpleDateFormat("dd/MM/yy").parse(startSemester);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    boolean checkParity(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.getFirstDayOfWeek();

        Calendar first = Calendar.getInstance();
        first.setTime(this.firstDay);

        int weekNum=0;
        for (; first.before(calendar); weekNum++) {
            first.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return weekNum%2==0;
    }
}
