package com.example.plantshome.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.plantshome.R;

/**
 * Intents that leave the app (Play Store, share sheet, email).
 * Every launch is guarded so a missing app shows a message instead of crashing.
 */
public final class AppIntents {

    private AppIntents() {
    }

    @NonNull
    public static String playStoreUrl(@NonNull Context context) {
        return "https://play.google.com/store/apps/details?id=" + context.getPackageName();
    }

    public static void openPlayStorePage(@NonNull Context context) {
        Intent market = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + context.getPackageName()));
        try {
            context.startActivity(market);
        } catch (ActivityNotFoundException noStoreApp) {
            startSafely(context, new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl(context))));
        }
    }

    public static void shareApp(@NonNull Context context) {
        String appName = context.getString(R.string.app_name);
        Intent send = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, appName)
                .putExtra(Intent.EXTRA_TEXT,
                        context.getString(R.string.share_message, appName, playStoreUrl(context)));
        startSafely(context, Intent.createChooser(send, context.getString(R.string.share_chooser_title)));
    }

    public static boolean sendFeedbackEmail(@NonNull Context context,
                                            @NonNull String from,
                                            @NonNull String feedback) {
        String appName = context.getString(R.string.app_name);
        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.feedback_email)})
                .putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject, appName))
                .putExtra(Intent.EXTRA_TEXT, context.getString(R.string.feedback_body, from, feedback));
        return startSafely(context, email);
    }

    public static boolean startSafely(@NonNull Context context, @NonNull Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.message_no_app_found, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
