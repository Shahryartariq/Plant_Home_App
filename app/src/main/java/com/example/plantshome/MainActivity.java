package com.example.plantshome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.plantshome.databinding.ActivityMainBinding;
import com.example.plantshome.reminder.ReminderScheduler;
import com.example.plantshome.ui.home.PlantSearchViewModel;
import com.example.plantshome.util.AppIntents;
import com.example.plantshome.util.AppNavigator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private PlantSearchViewModel search;

    private final ActivityResultLauncher<String> notificationPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    turnReminderOn();
                } else {
                    toast(R.string.reminder_permission_denied);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            AppNavigator.signOut(this);
            return;
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        search = new ViewModelProvider(this).get(PlantSearchViewModel.class);

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
        showUserInDrawer(user);
        closeDrawerOnBack();
    }

    private void showUserInDrawer(FirebaseUser user) {
        TextView title = binding.navView.getHeaderView(0).findViewById(R.id.navHeaderTitle);
        String name = !TextUtils.isEmpty(user.getDisplayName()) ? user.getDisplayName() : user.getEmail();
        if (title != null && !TextUtils.isEmpty(name)) {
            title.setText(name);
        }
    }

    private void closeDrawerOnBack() {
        OnBackPressedCallback closeDrawer = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, closeDrawer);
        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(@NonNull android.view.View drawerView) {
                closeDrawer.setEnabled(true);
            }

            @Override
            public void onDrawerClosed(@NonNull android.view.View drawerView) {
                closeDrawer.setEnabled(false);
            }
        });
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
        setUpSearch(menu.findItem(R.id.search));
        return true;
    }

    /** The search icon expands into a box that filters the plants on Home. */
    private void setUpSearch(MenuItem item) {
        SearchView searchView = (SearchView) item.getActionView();
        if (searchView == null) {
            return;
        }
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                search.setQuery(text);
                return true;
            }
        });
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem menuItem) {
                NavDestination current = navController.getCurrentDestination();
                if (current == null || current.getId() != R.id.nav_home) {
                    navController.popBackStack(R.id.nav_home, false);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem menuItem) {
                search.setQuery("");
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.notification) {
            showReminderDialog();
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ProfileActivity.class));
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

    private void showReminderDialog() {
        boolean on = ReminderScheduler.isEnabled(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.reminder_dialog_title)
                .setMessage(on ? R.string.reminder_dialog_message_on : R.string.reminder_dialog_message_off)
                .setPositiveButton(on ? R.string.reminder_turn_off : R.string.reminder_turn_on, (dialog, which) -> {
                    if (on) {
                        ReminderScheduler.disable(this);
                        toast(R.string.reminder_off);
                    } else {
                        requestReminder();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private void requestReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            return;
        }
        turnReminderOn();
    }

    private void turnReminderOn() {
        ReminderScheduler.enable(this);
        toast(R.string.reminder_on);
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
