package com.example.plantshome.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

/**
 * Pure-Java form checks, so they run in plain JVM unit tests.
 */
public final class InputValidator {

    /** Minimum length for new passwords (Firebase itself allows 6; existing accounts can still sign in). */
    public static final int MIN_PASSWORD_LENGTH = 8;

    // Same shape as android.util.Patterns.EMAIL_ADDRESS.
    private static final Pattern EMAIL = Pattern.compile(
            "[a-zA-Z0-9+._%\\-]{1,256}"
                    + "@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
                    + "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+");

    public enum Result { OK, REQUIRED, INVALID_EMAIL, TOO_SHORT, MISMATCH }

    private InputValidator() {
    }

    @NonNull
    public static Result required(@Nullable String value) {
        return isBlank(value) ? Result.REQUIRED : Result.OK;
    }

    @NonNull
    public static Result email(@Nullable String value) {
        if (isBlank(value)) {
            return Result.REQUIRED;
        }
        return EMAIL.matcher(value.trim()).matches() ? Result.OK : Result.INVALID_EMAIL;
    }

    /** For sign in: any non-empty password; Firebase decides if it is right. */
    @NonNull
    public static Result existingPassword(@Nullable String value) {
        return value == null || value.isEmpty() ? Result.REQUIRED : Result.OK;
    }

    /** For sign up and password changes. */
    @NonNull
    public static Result newPassword(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return Result.REQUIRED;
        }
        return value.length() < MIN_PASSWORD_LENGTH ? Result.TOO_SHORT : Result.OK;
    }

    @NonNull
    public static Result confirmation(@Nullable String password, @Nullable String confirmation) {
        if (confirmation == null || confirmation.isEmpty()) {
            return Result.REQUIRED;
        }
        return confirmation.equals(password) ? Result.OK : Result.MISMATCH;
    }

    private static boolean isBlank(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }
}
