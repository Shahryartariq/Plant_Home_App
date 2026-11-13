package com.example.plantshome.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * The toolbar search text, shared between MainActivity (the search box) and HomeFragment (the grid).
 */
public class PlantSearchViewModel extends ViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>("");

    @NonNull
    public LiveData<String> query() {
        return query;
    }

    public void setQuery(@NonNull String text) {
        query.setValue(text.trim());
    }
}
