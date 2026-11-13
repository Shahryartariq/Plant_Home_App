package com.example.plantshome;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * Test-only screen that shows one of the app's real layouts for {@link ScreenshotTest}.
 * With {@link #EXTRA_MAIN} it builds the home screen like MainActivity (toolbar, drawer,
 * navigation) without needing a signed-in Firebase user.
 */
public class ScreenshotHostActivity extends AppCompatActivity {

    static final String EXTRA_LAYOUT = "layout";
    static final String EXTRA_MAIN = "main";

    NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        boolean main = getIntent().getBooleanExtra(EXTRA_MAIN, false);
        setTheme(main ? R.style.Theme_PlantsHome_NoActionBar : R.style.Theme_PlantsHome);
        super.onCreate(savedInstanceState);
        if (!main) {
            setContentView(getIntent().getIntExtra(EXTRA_LAYOUT, R.layout.activity_splash));
            return;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (host != null) {
            navController = host.getNavController();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            AppBarConfiguration config = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_report, R.id.nav_taskpanel, R.id.nav_badge, R.id.nav_feedback)
                    .setOpenableLayout(drawer)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, config);
            NavigationUI.setupWithNavController(
                    (com.google.android.material.navigation.NavigationView) findViewById(R.id.nav_view),
                    navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getBooleanExtra(EXTRA_MAIN, false)) {
            getMenuInflater().inflate(R.menu.main_options, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
}
