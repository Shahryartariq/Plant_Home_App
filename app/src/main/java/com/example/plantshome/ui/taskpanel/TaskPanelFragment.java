package com.example.plantshome.ui.taskpanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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

import java.util.List;

/**
 * Today's watering tasks, one row per plant; ticks are saved for the day.
 */
public class TaskPanelFragment extends Fragment {

    private FragmentTaskPanelBinding binding;
    private CareLog log;
    private CheckBox[] checks;

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
        List<Plant> plants = PlantCatalog.taskPlants();

        for (int i = 0; i < checks.length; i++) {
            int task = i;
            Plant plant = plants.get(i);
            String text = getString(R.string.task_water, getString(plant.name));
            texts[i].setText(text);
            checks[i].setContentDescription(text);
            ViewGroup row = (ViewGroup) checks[i].getParent();
            ImageView image = findImage(row);
            if (image != null) {
                image.setImageResource(plant.image);
            }
            // Tapping anywhere on the row ticks it.
            row.setOnClickListener(v -> checks[task].toggle());
            checks[i].setOnCheckedChangeListener((box, checked) -> log.setTaskDone(task, checked));
        }

        // Completing the tasks earns a badge.
        binding.btnCompletedTask.setOnClickListener(v -> {
            log.setAllTasksDone(checks.length);
            log.completeDay();
            TopLevelNav.goTo(this, R.id.nav_badge);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < checks.length; i++) {
            checks[i].setChecked(log.isTaskDone(i));
        }
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
        binding = null;
    }
}
