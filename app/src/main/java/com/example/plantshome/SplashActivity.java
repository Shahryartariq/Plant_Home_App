package com.example.plantshome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.plantshome.databinding.ActivitySplashBinding;
import com.example.plantshome.util.AppNavigator;
import com.google.firebase.auth.FirebaseAuth;

// The app keeps its own branded splash screen by design.
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private static final long SPLASH_DELAY_MS = 2500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openNextScreen = this::openNextScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivitySplashBinding.inflate(getLayoutInflater()).getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(openNextScreen, SPLASH_DELAY_MS);
    }

    @Override
    protected void onStop() {
        // Don't jump to the next screen if the user left the app during the splash.
        handler.removeCallbacks(openNextScreen);
        super.onStop();
    }

    private void openNextScreen() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AppNavigator.routeSignedInUser(this);
        } else {
            startActivity(new Intent(this, GetStartedActivity.class));
            finish();
        }
    }
}
