package com.example.plantshome.reminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Turns the daily 9:00 plant-care reminder on and off.
 */
public final class ReminderScheduler {

    static final int REMINDER_HOUR = 9;
    private static final String WORK_NAME = "daily-care-reminder";
    private static final String FILE = "plantshome_reminder";
    private static final String KEY_ENABLED = "enabled";

    private ReminderScheduler() {
    }

    public static boolean isEnabled(@NonNull Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .getBoolean(KEY_ENABLED, false);
    }

    public static void enable(@NonNull Context context) {
        Context app = context.getApplicationContext();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(ReminderWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(millisUntilNext(REMINDER_HOUR, Calendar.getInstance()), TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(app).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, request);
        setEnabled(app, true);
    }

    public static void disable(@NonNull Context context) {
        Context app = context.getApplicationContext();
        WorkManager.getInstance(app).cancelUniqueWork(WORK_NAME);
        setEnabled(app, false);
    }

    /** Milliseconds from {@code now} until the next time the clock shows {@code hour}:00. */
    @VisibleForTesting
    static long millisUntilNext(int hour, @NonNull Calendar now) {
        Calendar next = (Calendar) now.clone();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_MONTH, 1);
        }
        return next.getTimeInMillis() - now.getTimeInMillis();
    }

    private static void setEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ENABLED, enabled).apply();
    }
}
