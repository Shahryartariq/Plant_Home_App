package com.example.plantshome.util;

import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.example.plantshome.R;

/**
 * Reads form fields and shows {@link InputValidator} results on them.
 */
public final class Forms {

    private Forms() {
    }

    /** Trimmed text, for names and emails. */
    @NonNull
    public static String text(@NonNull EditText field) {
        return raw(field).trim();
    }

    /** Untrimmed text, for passwords (spaces are allowed in passwords). */
    @NonNull
    public static String raw(@NonNull EditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    /**
     * Shows the error for {@code result} on {@code field}.
     *
     * @param requiredMessage message to show when the field is empty
     * @return true when the field is valid
     */
    public static boolean check(@NonNull EditText field,
                                @NonNull InputValidator.Result result,
                                @StringRes int requiredMessage) {
        if (result == InputValidator.Result.OK) {
            field.setError(null);
            return true;
        }
        Context context = field.getContext();
        String message;
        switch (result) {
            case INVALID_EMAIL:
                message = context.getString(R.string.error_email_invalid);
                break;
            case TOO_SHORT:
                message = context.getResources().getQuantityString(R.plurals.error_password_too_short,
                        InputValidator.MIN_PASSWORD_LENGTH, InputValidator.MIN_PASSWORD_LENGTH);
                break;
            case MISMATCH:
                message = context.getString(R.string.error_passwords_do_not_match);
                break;
            case REQUIRED:
            default:
                message = context.getString(requiredMessage);
                break;
        }
        field.setError(message);
        field.requestFocus();
        return false;
    }
}
