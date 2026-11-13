package com.example.plantshome.util;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.plantshome.R;

/**
 * Moves between drawer screens the same way the drawer does:
 * no duplicate copies on the back stack, and Back always leads to Home.
 */
public final class TopLevelNav {

    private TopLevelNav() {
    }

    public static void goTo(@NonNull Fragment from, @IdRes int destination) {
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.nav_home, false)
                .build();
        NavHostFragment.findNavController(from).navigate(destination, null, options);
    }
}
