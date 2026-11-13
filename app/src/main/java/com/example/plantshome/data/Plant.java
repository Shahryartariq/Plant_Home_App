package com.example.plantshome.data;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * One plant card on the home screen.
 *
 * @param index position of the card in the home grid; also used as the plant's id
 */
public final class Plant {

    public final int index;
    @StringRes
    public final int name;
    @StringRes
    public final int care;
    @DrawableRes
    public final int image;

    Plant(int index, @StringRes int name, @StringRes int care, @DrawableRes int image) {
        this.index = index;
        this.name = name;
        this.care = care;
        this.image = image;
    }
}
