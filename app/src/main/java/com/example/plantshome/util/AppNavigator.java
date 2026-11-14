package com.example.plantshome.util;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.plantshome.ChoicesActivity;
import com.example.plantshome.LoginActivity;
import com.example.plantshome.MainActivity;
import com.example.plantshome.VerifyEmailActivity;
import com.example.plantshome.auth.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * One place that decides where a user goes after auth changes:
 * signed out -> login, unverified -> verify email, first time -> choices, otherwise -> home.
 */
public final class AppNavigator {

    private AppNavigator() {
    }

    /** Opens {@code target} as the only screen in the back stack. */
    public static void startFresh(@NonNull Activity from, @NonNull Class<? extends Activity> target) {
        Intent intent = new Intent(from, target)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        from.startActivity(intent);
        from.finish();
    }

    public static void routeSignedInUser(@NonNull Activity from) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startFresh(from, LoginActivity.class);
        } else if (!user.isEmailVerified()) {
            startFresh(from, VerifyEmailActivity.class);
        } else if (OnboardingPrefs.isChoicesDone(from, user.getUid())) {
            startFresh(from, MainActivity.class);
        } else {
            startFresh(from, ChoicesActivity.class);
        }
    }

    public static void signOut(@NonNull Activity from) {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.forgetAccount(from);
        startFresh(from, LoginActivity.class);
    }
}
