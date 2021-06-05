package com.example.plantshome.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantshome.Activity_Login;
import com.example.plantshome.Activity_Profile;
import com.example.plantshome.R;

public class Fragment_Profile extends Fragment {

    private FragmentProfileViewModel mViewModel;

    public static Fragment_Profile newInstance() {
        return new Fragment_Profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__profile_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentProfileViewModel.class);
        // TODO: Use the ViewModel

        Intent intent = new Intent(getActivity(), Activity_Profile.class);
        startActivity(intent);

    }

}