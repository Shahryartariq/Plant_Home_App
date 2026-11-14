package com.example.plantshome.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CareLogTest {

    private SharedPreferences prefs;
    private String today;

    @Before
    public void setUp() {
        prefs = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("care-test", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
        today = "2026-11-13";
    }

    private CareLog log(String uid) {
        return new CareLog(prefs, uid, () -> today);
    }

    @Test
    public void careItems_areSavedForTheDay() {
        CareLog log = log("sherry");
        assertFalse(log.isDone(CareLog.Care.WATER));

        log.setDone(CareLog.Care.WATER, true);

        assertTrue(log("sherry").isDone(CareLog.Care.WATER));
        assertFalse(log("sherry").isDone(CareLog.Care.SUNLIGHT));
    }

    @Test
    public void ticks_resetOnANewDay_butBadgesStay() {
        CareLog log = log("sherry");
        log.setAllCareDone();
        log.setTaskDone(2, true);
        assertTrue(log.completeDay());

        today = "2026-11-14";
        CareLog tomorrow = log("sherry");

        assertFalse(tomorrow.isDone(CareLog.Care.WATER));
        assertFalse(tomorrow.isTaskDone(2));
        assertFalse(tomorrow.isDayComplete());
        assertEquals(1, tomorrow.badgeCount());
    }

    @Test
    public void aDayEarnsOnlyOneBadge() {
        CareLog log = log("sherry");

        assertTrue(log.completeDay());
        assertFalse(log.completeDay());
        assertEquals(1, log.badgeCount());

        today = "2026-11-14";
        assertTrue(log.completeDay());
        assertEquals(2, log.badgeCount());
    }

    @Test
    public void accountsAreKeptApart() {
        log("sherry").setDone(CareLog.Care.FERTILIZER, true);
        log("sherry").completeDay();

        CareLog other = log("someone-else");
        assertFalse(other.isDone(CareLog.Care.FERTILIZER));
        assertEquals(0, other.badgeCount());
    }

    @Test
    public void allTasksDone_ticksEveryRow() {
        CareLog log = log("sherry");
        log.setAllTasksDone(PlantCatalog.TASK_COUNT);
        for (int task = 0; task < PlantCatalog.TASK_COUNT; task++) {
            assertTrue(log.isTaskDone(task));
        }
    }
}
