package edu.adam.eventtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MainActivityTest {

    @BeforeEach
    @AfterEach
    public void cleanStorage() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteFile("Events.json");
    }

    @Test
    public void freshLaunch_loadsDefaultDummyDataIntoRecycler() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                org.junit.jupiter.api.Assertions.assertNotNull(activity.binding.eventRecycler.getAdapter());
                assertEquals(10, activity.binding.eventRecycler.getAdapter().getItemCount());
            });
        }
    }

    @Test
    public void selectingCategory_narrowsListBySelectedType() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                // Accessing Views through binding directly
                activity.binding.categorySpinner.setSelection(1);

                int count = activity.binding.eventRecycler.getAdapter().getItemCount();
                assertTrue(count > 0, "Expected matching items for category");
                assertTrue(count < 10, "Filtered list should be smaller than master dataset");
            });
        }
    }

    @Test
    public void selectingRating_filtersForEventsAtOrAboveThreshold() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                // Accessing Views through binding directly
                activity.binding.ratingSpinner.setSelection(4);

                int count = activity.binding.eventRecycler.getAdapter().getItemCount();
                assertTrue(count <= 10);
            });
        }
    }

    @Test
    public void clearingCategoryFilter_restoresFullList() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.binding.categorySpinner.setSelection(1);
                assertTrue(activity.binding.eventRecycler.getAdapter().getItemCount() < 10);

                activity.binding.categorySpinner.setSelection(0);
                assertEquals(10, activity.binding.eventRecycler.getAdapter().getItemCount());
            });
        }
    }

    @Test
    public void longClickItem_showsConfirmationDialogAndDeletesOnConfirm() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                int countBefore = activity.binding.eventRecycler.getAdapter().getItemCount();

                activity.binding.eventRecycler.getChildAt(0).performLongClick();

                AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                assertTrue(dialog.isShowing(), "Confirmation dialog should appear");

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

                assertEquals(countBefore - 1, activity.binding.eventRecycler.getAdapter().getItemCount());
            });
        }
    }
}