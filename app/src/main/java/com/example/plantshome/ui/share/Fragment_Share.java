package com.example.plantshome.ui.share;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantshome.R;

public class Fragment_Share extends Fragment {

    private FragmentShareViewModel mViewModel;

    public static Fragment_Share newInstance() {
        return new Fragment_Share();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__share_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentShareViewModel.class);
        // TODO: Use the ViewModel

        Intent shareIntent= new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert the string");
        String app_url = "http://planthomeapp.com/";
        shareIntent.putExtra(Intent.EXTRA_TEXT,app_url);
        startActivity( Intent.createChooser(shareIntent, "Share via"));
    }

}