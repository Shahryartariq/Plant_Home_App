package com.example.plantshome;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.plantshome.databinding.ActivityMainBinding;
import com.example.plantshome.util.AppIntents;
import com.example.plantshome.util.AppNavigator;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            AppNavigator.signOut(this);
            return;
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHost == null) {
            throw new IllegalStateException("nav_host_fragment is missing from content_main.xml");
        }
        navController = navHost.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_taskpanel, R.id.nav_badge, R.id.nav_feedback)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Profile, Rate, Share and Logout are actions, not screens in the nav graph.
        binding.navView.setNavigationItemSelectedListener(this::onDrawerItemSelected);
    }

    private boolean onDrawerItemSelected(@NonNull MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return false;
        }
        if (id == R.id.nav_rate) {
            AppIntents.openPlayStorePage(this);
            return false;
        }
        if (id == R.id.nav_share) {
            AppIntents.shareApp(this);
            return false;
        }
        if (id == R.id.nav_logout) {
            AppNavigator.signOut(this);
            return false;
        }
        return NavigationUI.onNavDestinationSelected(item, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search || id == R.id.notification || id == R.id.action_settings) {
            toast(R.string.message_coming_soon);
            return true;
        }
        if (id == R.id.action_rate) {
            AppIntents.openPlayStorePage(this);
            return true;
        }
        if (id == R.id.exit) {
            confirmExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_exit_title)
                .setMessage(R.string.dialog_exit_message)
                .setCancelable(false)
                // Close every screen; the next launch starts again from the splash screen.
                .setPositiveButton(R.string.dialog_yes, (dialog, which) -> finishAffinity())
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }
}
