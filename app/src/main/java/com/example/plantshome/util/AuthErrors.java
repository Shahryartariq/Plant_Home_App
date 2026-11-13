package com.example.plantshome.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.plantshome.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 * Turns Firebase Auth exceptions into short, user-facing messages
 * instead of showing raw exception text.
 */
public final class AuthErrors {

    private static final String TAG = "AuthErrors";

    private AuthErrors() {
    }

    @NonNull
    public static String message(@NonNull Context context, @Nullable Exception error) {
        Log.w(TAG, "Auth request failed", error);
        if (error instanceof FirebaseNetworkException) {
            return context.getString(R.string.message_network_error);
        }
        if (error instanceof FirebaseTooManyRequestsException) {
            return context.getString(R.string.message_too_many_requests);
        }
        if (error instanceof FirebaseAuthWeakPasswordException) {
            return context.getResources().getQuantityString(R.plurals.error_password_too_short,
                    InputValidator.MIN_PASSWORD_LENGTH, InputValidator.MIN_PASSWORD_LENGTH);
        }
        if (error instanceof FirebaseAuthUserCollisionException) {
            return context.getString(R.string.message_email_in_use);
        }
        if (error instanceof FirebaseAuthRecentLoginRequiredException) {
            return context.getString(R.string.message_recent_login_required);
        }
        if (error instanceof FirebaseAuthInvalidUserException
                || error instanceof FirebaseAuthInvalidCredentialsException) {
            return context.getString(R.string.message_wrong_credentials);
        }
        return context.getString(R.string.message_generic_error);
    }

    public static boolean needsRecentLogin(@Nullable Exception error) {
        return error instanceof FirebaseAuthRecentLoginRequiredException;
    }
}
