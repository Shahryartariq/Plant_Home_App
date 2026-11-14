package com.example.plantshome.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantshome.PlantDetailActivity;
import com.example.plantshome.R;
import com.example.plantshome.data.Plant;
import com.example.plantshome.data.PlantCatalog;
import com.example.plantshome.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    /** The cards in their original order, so filtering can put them back. */
    private final List<View> cards = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup grid = binding.mainGridLayout;
        cards.clear();
        List<Plant> plants = PlantCatalog.all();
        for (int i = 0; i < grid.getChildCount() && i < plants.size(); i++) {
            View card = grid.getChildAt(i);
            Plant plant = plants.get(i);
            bindCard(card, plant);
            card.setOnClickListener(v ->
                    startActivity(PlantDetailActivity.intent(requireContext(), plant.index)));
            cards.add(card);
        }

        new ViewModelProvider(requireActivity()).get(PlantSearchViewModel.class)
                .query().observe(getViewLifecycleOwner(), this::filter);
    }

    /** Shows only the cards whose plant name contains {@code query}. */
    private void filter(@Nullable String query) {
        if (binding == null) {
            return;
        }
        String needle = query == null ? "" : query.toLowerCase(Locale.getDefault());
        ViewGroup grid = binding.mainGridLayout;
        // GridLayout places children in order, so re-add the matches instead of hiding the rest.
        grid.removeAllViews();
        int matches = 0;
        for (int i = 0; i < cards.size(); i++) {
            String name = getString(PlantCatalog.all().get(i).name).toLowerCase(Locale.getDefault());
            if (needle.isEmpty() || name.contains(needle)) {
                grid.addView(cards.get(i));
                matches++;
            }
        }
        // With no matches the grid is empty, so the title explains why.
        binding.textHome.setText(matches == 0
                ? getString(R.string.search_no_results, query)
                : getString(R.string.home_title));
    }

    private static void bindCard(View card, Plant plant) {
        ImageView image = findFirst(card, ImageView.class);
        TextView name = findFirst(card, TextView.class);
        if (image != null) {
            image.setImageResource(plant.image);
        }
        if (name != null) {
            name.setText(plant.name);
        }
        card.setContentDescription(card.getContext().getString(plant.name));
    }

    @Nullable
    private static <T extends View> T findFirst(View view, Class<T> type) {
        if (type.isInstance(view)) {
            return type.cast(view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                T found = findFirst(group.getChildAt(i), type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cards.clear();
        binding = null;
    }
}
