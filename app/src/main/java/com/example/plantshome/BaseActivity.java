package com.example.plantshome;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Base for every screen.
 *
 * <p>From Android 15 (targetSdk 35+) apps always draw edge-to-edge. To keep the original look,
 * this pads the content so nothing sits under the status bar, navigation bar or keyboard, and
 * paints only the bar strips in the app's green, like the old green status bar.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View content = findViewById(android.R.id.content);
        if (content == null) {
            return;
        }
        SystemBarsDrawable bars = new SystemBarsDrawable(ContextCompat.getColor(this, R.color.green));
        content.setBackground(bars);
        ViewCompat.setOnApplyWindowInsetsListener(content, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            int bottom = Math.max(insets.bottom, ime.bottom);
            view.setPadding(insets.left, insets.top, insets.right, bottom);
            bars.setInsets(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(content);
    }

    protected void toast(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void toast(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
