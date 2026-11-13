package com.example.plantshome.reminder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ReminderSchedulerTest {

    private static Calendar at(int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Karachi"));
        calendar.clear();
        calendar.set(2026, Calendar.NOVEMBER, 13, hour, minute, 0);
        return calendar;
    }

    @Test
    public void beforeNine_waitsUntilNineToday() {
        assertEquals(TimeUnit.MINUTES.toMillis(90),
                ReminderScheduler.millisUntilNext(9, at(7, 30)));
    }

    @Test
    public void afterNine_waitsUntilNineTomorrow() {
        assertEquals(TimeUnit.HOURS.toMillis(23),
                ReminderScheduler.millisUntilNext(9, at(10, 0)));
    }

    @Test
    public void exactlyNine_waitsAFullDay() {
        assertEquals(TimeUnit.DAYS.toMillis(1),
                ReminderScheduler.millisUntilNext(9, at(9, 0)));
    }
}
