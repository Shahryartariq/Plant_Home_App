package com.example.plantshome;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.example.plantshome.databinding.ActivityChoicesBinding;
import com.example.plantshome.util.AppNavigator;
import com.example.plantshome.util.OnboardingPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Pick the plants you like. Selected plants use the design's green chip; the rest stay white.
 */
public class ChoicesActivity extends BaseActivity {

    /** Selected in the original design, so a new account starts with them picked. */
    private static final Set<String> DEFAULT_CHOICES =
            new HashSet<>(Arrays.asList("adonis", "ambassador_allium", "alberta_spruce"));

    private static final String KEY_SELECTED = "selected";

    private final Map<String, Button> buttons = new LinkedHashMap<>();
    private Set<String> selected;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            AppNavigator.signOut(this);
            return;
        }
        uid = user.getUid();
        ActivityChoicesBinding binding = ActivityChoicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttons.put("boston_ivy", binding.button3);
        buttons.put("black_eyed_susan", binding.button17);
        buttons.put("adonis", binding.button6);
        buttons.put("butterfly_weed", binding.button4);
        buttons.put("ambassador_allium", binding.button15);
        buttons.put("aloe_vera", binding.button2);
        buttons.put("alberta_spruce", binding.button13);
        buttons.put("bougainvillea", binding.button7);
        buttons.put("bunchberry", binding.button5);

        selected = OnboardingPrefs.choices(this, uid, DEFAULT_CHOICES);
        if (savedInstanceState != null && savedInstanceState.getStringArray(KEY_SELECTED) != null) {
            selected = new HashSet<>(Arrays.asList(savedInstanceState.getStringArray(KEY_SELECTED)));
        }
        for (Map.Entry<String, Button> entry : buttons.entrySet()) {
            String key = entry.getKey();
            entry.getValue().setOnClickListener(v -> {
                if (!selected.remove(key)) {
                    selected.add(key);
                }
                render();
            });
        }
        render();

        binding.btnRegister.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                toast(R.string.choices_pick_one);
                return;
            }
            OnboardingPrefs.saveChoices(this, uid, selected);
            AppNavigator.startFresh(this, MainActivity.class);
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selected != null) {
            outState.putStringArray(KEY_SELECTED, selected.toArray(new String[0]));
        }
    }

    private void render() {
        ColorStateList green = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green));
        ColorStateList white = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white));
        for (Map.Entry<String, Button> entry : buttons.entrySet()) {
            boolean on = selected.contains(entry.getKey());
            Button button = entry.getValue();
            ViewCompat.setBackgroundTintList(button, on ? green : white);
            button.setTextColor(on ? white : green);
            button.setSelected(on);
        }
    }
}
