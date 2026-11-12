package com.example.plantshome.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Remembers, per account, that the user has picked their plant choices,
 * so the choices screen is shown only once.
 */
public final class OnboardingPrefs {

    private static final String FILE = "plantshome_onboarding";
    private static final String KEY_CHOICES_DONE = "choices_done_";

    private OnboardingPrefs() {
    }

    public static boolean isChoicesDone(@NonNull Context context, @NonNull String uid) {
        return prefs(context).getBoolean(KEY_CHOICES_DONE + uid, false);
    }

    public static void setChoicesDone(@NonNull Context context, @NonNull String uid) {
        prefs(context).edit().putBoolean(KEY_CHOICES_DONE + uid, true).apply();
    }

    public static void clear(@NonNull Context context, @NonNull String uid) {
        prefs(context).edit().remove(KEY_CHOICES_DONE + uid).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
