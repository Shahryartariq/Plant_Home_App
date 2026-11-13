package com.example.plantshome.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Per-account onboarding state: whether the choices screen is done, and which plants were picked.
 */
public final class OnboardingPrefs {

    private static final String FILE = "plantshome_onboarding";
    private static final String KEY_CHOICES_DONE = "choices_done_";
    private static final String KEY_CHOICES = "choices_";

    private OnboardingPrefs() {
    }

    public static boolean isChoicesDone(@NonNull Context context, @NonNull String uid) {
        return prefs(context).getBoolean(KEY_CHOICES_DONE + uid, false);
    }

    /**
     * The plants picked on the choices screen.
     *
     * @param defaults returned when the account hasn't picked anything yet
     */
    @NonNull
    public static Set<String> choices(@NonNull Context context, @NonNull String uid,
                                      @NonNull Set<String> defaults) {
        Set<String> saved = prefs(context).getStringSet(KEY_CHOICES + uid, null);
        // Copy: the set returned by SharedPreferences must not be modified.
        return new HashSet<>(saved == null ? defaults : saved);
    }

    public static void saveChoices(@NonNull Context context, @NonNull String uid,
                                   @NonNull Set<String> choices) {
        prefs(context).edit()
                .putStringSet(KEY_CHOICES + uid, Collections.unmodifiableSet(new HashSet<>(choices)))
                .putBoolean(KEY_CHOICES_DONE + uid, true)
                .apply();
    }

    public static void clear(@NonNull Context context, @NonNull String uid) {
        prefs(context).edit()
                .remove(KEY_CHOICES_DONE + uid)
                .remove(KEY_CHOICES + uid)
                .apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
