package com.example.plantshome.ui.badge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plantshome.R;
import com.example.plantshome.databinding.FragmentBadgeBinding;
import com.example.plantshome.util.TopLevelNav;

public class BadgeFragment extends Fragment {

    private FragmentBadgeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBadgeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // "Start" begins the next day from the home screen.
        binding.btnbadgestart.setOnClickListener(v -> TopLevelNav.goTo(this, R.id.nav_home));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
