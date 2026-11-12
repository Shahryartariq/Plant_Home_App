package com.example.plantshome.ui.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plantshome.R;
import com.example.plantshome.ThanksActivity;
import com.example.plantshome.databinding.FragmentFeedbackBinding;
import com.example.plantshome.util.AppIntents;
import com.example.plantshome.util.Forms;
import com.example.plantshome.util.InputValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FeedbackFragment extends Fragment {

    private FragmentFeedbackBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null && Forms.text(binding.userName).isEmpty()) {
            binding.userName.setText(user.getEmail());
        }
        binding.btnSubmitFeedback.setOnClickListener(v -> submit());
    }

    private void submit() {
        String email = Forms.text(binding.userName);
        String feedback = Forms.text(binding.myFeedback);
        boolean valid = Forms.check(binding.userName, InputValidator.email(email), R.string.error_email_required)
                && Forms.check(binding.myFeedback, InputValidator.required(feedback),
                R.string.error_feedback_required);
        if (!valid) {
            return;
        }
        // Thanks screen first, then the email app on top of it; Back returns to Thanks.
        startActivity(new Intent(requireContext(), ThanksActivity.class));
        if (AppIntents.sendFeedbackEmail(requireContext(), email, feedback)) {
            binding.myFeedback.setText(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
