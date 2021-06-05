package com.example.plantshome.ui.Logout;

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
import com.example.plantshome.R;
import com.google.firebase.auth.FirebaseAuth;

public class Fragment_Logout extends Fragment {

    private FragmentLogoutViewModel mViewModel;
    FirebaseAuth auth;


    public static Fragment_Logout newInstance() {
        return new Fragment_Logout();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__logout_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentLogoutViewModel.class);
        // TODO: Use the ViewModel
        auth = FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().signOut();


                if (auth.getCurrentUser() != null)
                    auth.signOut();
                Intent intent = new Intent(getActivity(), Activity_Login.class);
                startActivity(intent);


    }

}