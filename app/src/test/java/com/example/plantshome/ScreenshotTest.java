package com.example.plantshome;

import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.core.app.ApplicationProvider;

import com.example.plantshome.data.CareLog;
import com.example.plantshome.util.Session;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Renders the app's real layouts to JPEGs for the README.
 *
 * <p>Skipped in normal test runs. Run with:
 * {@code ./gradlew testDebugUnitTest --tests '*ScreenshotTest' -Pscreenshots=/abs/path/docs/screenshots}
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w393dp-h851dp-xhdpi")
public class ScreenshotTest {

    private File outDir;

    @Before
    public void setUp() {
        String dir = System.getProperty("plantshome.screenshots", "");
        Assume.assumeFalse("Pass -Pscreenshots=<dir> to render screenshots", dir.isEmpty());
        outDir = new File(dir);
        //noinspection ResultOfMethodCallIgnored
        outDir.mkdirs();

        Context context = ApplicationProvider.getApplicationContext();
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context, new FirebaseOptions.Builder()
                    .setApplicationId("1:000000000000:android:0000000000000000")
                    .setApiKey("screenshots-only")
                    .setProjectId("plantshome-screenshots")
                    .build());
        }
    }

    @Test
    public void splash() throws IOException {
        layout("01-splash", R.layout.activity_splash);
    }

    @Test
    public void getStarted() throws IOException {
        layout("02-get-started", R.layout.activity_getstarted);
    }

    @Test
    public void login() throws IOException {
        layout("03-login", R.layout.activity_login);
    }

    @Test
    public void signup() throws IOException {
        layout("04-signup", R.layout.activity_signup);
    }

    @Test
    public void choices() throws IOException {
        layout("05-choices", R.layout.activity_choices);
    }

    @Test
    public void home() throws IOException {
        main("06-home", 0, false);
    }

    @Test
    public void drawer() throws IOException {
        main("07-drawer", 0, true);
    }

    @Test
    public void plantDetail() throws IOException {
        layout("08-plant-detail", R.layout.activity_detail);
    }

    @Test
    public void dailyReport() throws IOException {
        CareLog log = CareLog.forUser(ApplicationProvider.getApplicationContext(), Session.uid());
        log.setDone(CareLog.Care.WATER, true);
        log.setDone(CareLog.Care.SUNLIGHT, true);
        main("09-daily-report", R.id.nav_report, false);
    }

    @Test
    public void tasks() throws IOException {
        CareLog log = CareLog.forUser(ApplicationProvider.getApplicationContext(), Session.uid());
        log.setTaskDone(0, true);
        log.setTaskDone(1, true);
        main("10-tasks", R.id.nav_taskpanel, false);
    }

    @Test
    public void badge() throws IOException {
        CareLog.forUser(ApplicationProvider.getApplicationContext(), Session.uid()).completeDay();
        main("11-badge", R.id.nav_badge, false);
    }

    @Test
    public void profile() throws IOException {
        ScreenshotHostActivity activity = start(new Intent()
                .putExtra(ScreenshotHostActivity.EXTRA_LAYOUT, R.layout.activity_profile));
        ((TextView) activity.findViewById(R.id.profileName))
                .setText(activity.getString(R.string.profile_name, "Sherry"));
        ((TextView) activity.findViewById(R.id.profileEmail))
                .setText(activity.getString(R.string.profile_email, "sherry@example.com"));
        capture(activity, "12-profile");
    }

    @Test
    public void feedback() throws IOException {
        main("13-feedback", R.id.nav_feedback, false);
    }

    private void layout(String name, @LayoutRes int layout) throws IOException {
        capture(start(new Intent().putExtra(ScreenshotHostActivity.EXTRA_LAYOUT, layout)), name);
    }

    private void main(String name, @IdRes int destination, boolean openDrawer) throws IOException {
        ScreenshotHostActivity activity = start(new Intent().putExtra(ScreenshotHostActivity.EXTRA_MAIN, true));
        if (destination != 0 && activity.navController != null) {
            activity.navController.navigate(destination);
        }
        if (openDrawer) {
            ((DrawerLayout) activity.findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START, false);
        }
        capture(activity, name);
    }

    private ScreenshotHostActivity start(Intent extras) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ScreenshotHostActivity.class)
                .putExtras(extras);
        ActivityController<ScreenshotHostActivity> controller =
                Robolectric.buildActivity(ScreenshotHostActivity.class, intent).setup();
        shadowOf(Looper.getMainLooper()).idle();
        return controller.get();
    }

    private void capture(ScreenshotHostActivity activity, String name) throws IOException {
        shadowOf(Looper.getMainLooper()).idle();
        View root = activity.getWindow().getDecorView();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, width, height);
        shadowOf(Looper.getMainLooper()).idle();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        root.draw(new Canvas(bitmap));
        try (OutputStream out = new FileOutputStream(new File(outDir, name + ".jpg"))) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
        }
    }
}
