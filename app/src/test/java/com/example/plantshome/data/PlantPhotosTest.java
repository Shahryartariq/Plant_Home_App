package com.example.plantshome.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlantPhotosTest {

    @Test
    public void sampleSize_keepsAtLeastTheTargetWidth() {
        assertEquals(1, PlantPhotos.sampleSize(1000, 1080));
        assertEquals(1, PlantPhotos.sampleSize(2000, 1080));
        assertEquals(2, PlantPhotos.sampleSize(4000, 1080));
        assertEquals(4, PlantPhotos.sampleSize(4320, 1080));
        assertEquals(8, PlantPhotos.sampleSize(9000, 1080));
    }
}
