package com.example.plantshome.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.example.plantshome.R;
import com.example.plantshome.data.CareLog;
import com.example.plantshome.databinding.FragmentReportBinding;
import com.example.plantshome.util.Session;
import com.example.plantshome.util.TopLevelNav;

/**
 * Today's care checklist: tap Water / Sunlight / Fertilizer / Cuttings as you do them.
 */
public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private CareLog log;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log = CareLog.forUser(requireContext(), Session.uid());

        bind(binding.reportbtn1, CareLog.Care.WATER, R.string.report_water);
        bind(binding.reportbtn2, CareLog.Care.SUNLIGHT, R.string.report_sunlight);
        bind(binding.reportbtn3, CareLog.Care.FERTILIZER, R.string.report_fertilizer);
        bind(binding.reportbtn4, CareLog.Care.CUTTINGS, R.string.report_cuttings);

        // Finishing the day's report earns a badge.
        binding.allDone.setOnClickListener(v -> {
            log.setAllCareDone();
            log.completeDay();
            TopLevelNav.goTo(this, R.id.nav_badge);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // The day may have changed while the screen was in the background.
        render();
    }

    private void bind(Button button, CareLog.Care care, @StringRes int label) {
        button.setTag(new Item(care, label));
        button.setOnClickListener(v -> {
            log.setDone(care, !log.isDone(care));
            render();
        });
    }

    private void render() {
        if (binding == null) {
            return;
        }
        for (Button button : new Button[]{binding.reportbtn1, binding.reportbtn2,
                binding.reportbtn3, binding.reportbtn4}) {
            Item item = (Item) button.getTag();
            boolean done = log.isDone(item.care);
            String label = getString(item.label);
            button.setText(done ? getString(R.string.report_done_item, label) : label);
            button.setSelected(done);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static final class Item {
        final CareLog.Care care;
        @StringRes
        final int label;

        Item(CareLog.Care care, @StringRes int label) {
            this.care = care;
            this.label = label;
        }
    }
}
