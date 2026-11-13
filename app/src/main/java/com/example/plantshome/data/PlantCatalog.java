package com.example.plantshome.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.plantshome.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The plants on the home screen, in the same order as the cards in fragment_home.xml.
 */
public final class PlantCatalog {

    /** How many plants the Tasks panel lists (it has five rows). */
    public static final int TASK_COUNT = 5;

    private static final List<Plant> PLANTS = Collections.unmodifiableList(Arrays.asList(
            new Plant(0, R.string.plant_monstera_adansonii, R.string.care_monstera_adansonii, R.drawable.p12),
            new Plant(1, R.string.plant_jade_pothos, R.string.care_jade_pothos, R.drawable.p8),
            new Plant(2, R.string.plant_calathea_freddie, R.string.care_calathea_freddie, R.drawable.p10),
            new Plant(3, R.string.plant_calathea, R.string.care_calathea, R.drawable.p9),
            new Plant(4, R.string.plant_lancifolia, R.string.care_lancifolia, R.drawable.p10),
            new Plant(5, R.string.plant_monstera, R.string.care_monstera, R.drawable.p8),
            new Plant(6, R.string.plant_pothos, R.string.care_pothos, R.drawable.p9),
            new Plant(7, R.string.plant_monstera_adansonii, R.string.care_monstera_adansonii, R.drawable.p11),
            new Plant(8, R.string.plant_jade_pothos, R.string.care_jade_pothos, R.drawable.p12),
            new Plant(9, R.string.plant_jade_pothos, R.string.care_jade_pothos, R.drawable.p9)));

    private PlantCatalog() {
    }

    @NonNull
    public static List<Plant> all() {
        return PLANTS;
    }

    /** The plant for a card index, or null if the index is out of range. */
    @Nullable
    public static Plant get(int index) {
        return index >= 0 && index < PLANTS.size() ? PLANTS.get(index) : null;
    }

    /** The plants shown as watering tasks. */
    @NonNull
    public static List<Plant> taskPlants() {
        return PLANTS.subList(0, TASK_COUNT);
    }
}
