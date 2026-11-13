package com.example.plantshome.ui.taskpanel;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plantshome.R;
import com.example.plantshome.data.CareLog;
import com.example.plantshome.data.Plant;
import com.example.plantshome.data.PlantCatalog;
import com.example.plantshome.databinding.FragmentTaskPanelBinding;
import com.example.plantshome.util.Session;
import com.example.plantshome.util.TopLevelNav;

import java.util.ArrayList;
import java.util.List;

/**
 * Today's watering tasks, as in the original design: open tasks above the
 * "Completed Task" pill, ticked tasks below it. Ticks are saved for the day.
 */
public class TaskPanelFragment extends Fragment {

    // Spacing from the original layout.
    private static final int FIRST_TOP_DP = 100;
    private static final int ROW_TOP_DP = 20;
    private static final int AROUND_PILL_DP = 25;

    private FragmentTaskPanelBinding binding;
    private CareLog log;
    private CheckBox[] checks;
    private View[] rows;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskPanelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log = CareLog.forUser(requireContext(), Session.uid());

        checks = new CheckBox[]{binding.taskCheck1, binding.taskCheck2, binding.taskCheck3,
                binding.taskCheck4, binding.taskCheck5};
        TextView[] texts = {binding.taskText1, binding.taskText2, binding.taskText3,
                binding.taskText4, binding.taskText5};
        rows = new View[checks.length];
        List<Plant> plants = PlantCatalog.taskPlants();

        for (int i = 0; i < checks.length; i++) {
            int task = i;
            Plant plant = plants.get(i);
            String text = getString(R.string.task_water, getString(plant.name));
            texts[i].setText(text);
            checks[i].setContentDescription(text);
            rows[i] = (View) checks[i].getParent();
            ImageView image = findImage((ViewGroup) rows[i]);
            if (image != null) {
                image.setImageResource(plant.image);
            }
            checks[i].setOnClickListener(v -> onTaskToggled(task, checks[task].isChecked()));
            // Tapping anywhere on the row ticks it too.
            rows[i].setOnClickListener(v -> {
                checks[task].toggle();
                onTaskToggled(task, checks[task].isChecked());
            });
        }

        // The pill completes every task at once.
        binding.btnCompletedTask.setOnClickListener(v -> {
            log.setAllTasksDone(checks.length);
            finishDay();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void onTaskToggled(int task, boolean done) {
        log.setTaskDone(task, done);
        if (allDone()) {
            finishDay();
        } else {
            render();
        }
    }

    private void finishDay() {
        log.completeDay();
        TopLevelNav.goTo(this, R.id.nav_badge);
    }

    private boolean allDone() {
        for (int i = 0; i < checks.length; i++) {
            if (!log.isTaskDone(i)) {
                return false;
            }
        }
        return true;
    }

    /** Puts open tasks above the pill and done tasks below it. */
    private void render() {
        if (binding == null) {
            return;
        }
        List<View> order = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            if (!log.isTaskDone(i)) {
                order.add(rows[i]);
            }
        }
        View pill = binding.btnCompletedTask;
        order.add(pill);
        for (int i = 0; i < rows.length; i++) {
            boolean done = log.isTaskDone(i);
            checks[i].setChecked(done);
            checks[i].jumpDrawablesToCurrentState();
            if (done) {
                order.add(rows[i]);
            }
        }

        LinearLayout list = binding.getRoot();
        list.removeAllViews();
        for (int i = 0; i < order.size(); i++) {
            View child = order.get(i);
            int top;
            if (i == 0) {
                top = FIRST_TOP_DP;
            } else if (child == pill || order.get(i - 1) == pill) {
                top = AROUND_PILL_DP;
            } else {
                top = ROW_TOP_DP;
            }
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            params.topMargin = dp(top);
            list.addView(child, params);
        }
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics()));
    }

    @Nullable
    private static ImageView findImage(ViewGroup row) {
        for (int i = 0; i < row.getChildCount(); i++) {
            if (row.getChildAt(i) instanceof ImageView) {
                return (ImageView) row.getChildAt(i);
            }
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        checks = null;
        rows = null;
        binding = null;
    }
}
