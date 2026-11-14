package com.example.plantshome.util;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The signed-in account's id, used to keep each account's choices and progress apart.
 */
public final class Session {

    /** Used when nobody is signed in, so screens still work (e.g. right after sign out). */
    public static final String SIGNED_OUT = "signed-out";

    private Session() {
    }

    @NonNull
    public static String uid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user == null ? SIGNED_OUT : user.getUid();
    }
}
