package com.example.plantshome.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import java.util.List;

public class PlantCatalogTest {

    @Test
    public void hasOnePlantPerHomeCard_inOrder() {
        List<Plant> plants = PlantCatalog.all();
        assertEquals(10, plants.size());
        for (int i = 0; i < plants.size(); i++) {
            assertEquals(i, plants.get(i).index);
            assertSame(plants.get(i), PlantCatalog.get(i));
        }
    }

    @Test
    public void outOfRangeIndex_returnsNull() {
        assertNull(PlantCatalog.get(-1));
        assertNull(PlantCatalog.get(10));
    }

    @Test
    public void tasksUseTheFirstFivePlants() {
        List<Plant> tasks = PlantCatalog.taskPlants();
        assertEquals(PlantCatalog.TASK_COUNT, tasks.size());
        assertSame(PlantCatalog.get(0), tasks.get(0));
        assertSame(PlantCatalog.get(4), tasks.get(4));
    }
}
