package com.example.plantshome.ui.taskpanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plantshome.R;
import com.example.plantshome.databinding.FragmentTaskPanelBinding;
import com.example.plantshome.util.TopLevelNav;

public class TaskPanelFragment extends Fragment {

    private FragmentTaskPanelBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskPanelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Completing the tasks earns a badge.
        binding.btnCompletedTask.setOnClickListener(v -> TopLevelNav.goTo(this, R.id.nav_badge));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
