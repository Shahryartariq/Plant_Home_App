package com.example.plantshome.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Today's plant care for one account: the Daily Report items, the ticked tasks,
 * whether the day is complete, and how many badges have been earned in total.
 *
 * <p>Daily items reset automatically when the date changes; badges are kept.
 */
public final class CareLog {

    /** The four Daily Report buttons. */
    public enum Care { WATER, SUNLIGHT, FERTILIZER, CUTTINGS }

    /** Supplies today's date as yyyy-MM-dd. */
    public interface Clock {
        @NonNull
        String today();
    }

    private static final String FILE = "plantshome_care";

    private final SharedPreferences prefs;
    private final String prefix;
    private final Clock clock;

    @VisibleForTesting
    CareLog(@NonNull SharedPreferences prefs, @NonNull String uid, @NonNull Clock clock) {
        this.prefs = prefs;
        this.prefix = uid + ".";
        this.clock = clock;
    }

    @NonNull
    public static CareLog forUser(@NonNull Context context, @NonNull String uid) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(FILE, Context.MODE_PRIVATE);
        return new CareLog(prefs, uid, CareLog::localToday);
    }

    /** Removes all of one account's progress (used when the account is deleted). */
    public static void clear(@NonNull Context context, @NonNull String uid) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(uid + ".")) {
                editor.remove(key);
            }
        }
        editor.apply();
    }

    public boolean isDone(@NonNull Care care) {
        ensureToday();
        return prefs.getBoolean(careKey(care), false);
    }

    public void setDone(@NonNull Care care, boolean done) {
        ensureToday();
        prefs.edit().putBoolean(careKey(care), done).apply();
    }

    public void setAllCareDone() {
        ensureToday();
        SharedPreferences.Editor editor = prefs.edit();
        for (Care care : Care.values()) {
            editor.putBoolean(careKey(care), true);
        }
        editor.apply();
    }

    public boolean isTaskDone(int task) {
        ensureToday();
        return prefs.getBoolean(taskKey(task), false);
    }

    public void setTaskDone(int task, boolean done) {
        ensureToday();
        prefs.edit().putBoolean(taskKey(task), done).apply();
    }

    public void setAllTasksDone(int taskCount) {
        ensureToday();
        SharedPreferences.Editor editor = prefs.edit();
        for (int task = 0; task < taskCount; task++) {
            editor.putBoolean(taskKey(task), true);
        }
        editor.apply();
    }

    public boolean isDayComplete() {
        ensureToday();
        return prefs.getBoolean(prefix + "complete", false);
    }

    /**
     * Marks today as complete. A day earns one badge, however often this is called.
     *
     * @return true if this call earned a new badge
     */
    public boolean completeDay() {
        if (isDayComplete()) {
            return false;
        }
        prefs.edit()
                .putBoolean(prefix + "complete", true)
                .putInt(prefix + "badges", badgeCount() + 1)
                .apply();
        return true;
    }

    public int badgeCount() {
        return prefs.getInt(prefix + "badges", 0);
    }

    /** Clears yesterday's ticks the first time the log is used on a new day. */
    private void ensureToday() {
        String today = clock.today();
        if (today.equals(prefs.getString(prefix + "date", null))) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(prefix + "care.") || key.startsWith(prefix + "task.")) {
                editor.remove(key);
            }
        }
        editor.remove(prefix + "complete")
                .putString(prefix + "date", today)
                .apply();
    }

    private String careKey(Care care) {
        return prefix + "care." + care.name();
    }

    private String taskKey(int task) {
        return prefix + "task." + task;
    }

    @NonNull
    private static String localToday() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }
}
