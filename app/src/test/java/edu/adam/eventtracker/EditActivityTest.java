package edu.adam.eventtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import edu.adam.eventtracker.event.Event;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EditActivityTest {

    @Test
    public void launchWithExistingEvent_preFillsInputFields() {
        Event event = new Event("CAFE", "Study Session", "Library", "2026-10-18", "Focus time", 4);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditActivity.class);
        intent.putExtra("INDEX", 0);
        intent.putExtra("EDIT_EVENT", event);

        try (ActivityScenario<EditActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Accessing form fields directly through binding
                assertEquals("Study Session", activity.binding.eventNameField.getText().toString());
                assertEquals("Library", activity.binding.eventAddressField.getText().toString());
                assertEquals("2026-10-18", activity.binding.eventDateField.getText().toString());
                assertEquals("Focus time", activity.binding.eventNotesField.getText().toString());
            });
        }
    }

    @Test
    public void invalidForm_blocksSubmissionAndShowsToast() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditActivity.class);

        try (ActivityScenario<EditActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                activity.editEvent();

                assertFalse(activity.isFinishing(), "Activity should stay open when validation fails");
                assertEquals(
                        activity.getString(R.string.please_fill_out_the_requested_fields),
                        ShadowToast.getTextOfLatestToast()
                );            });
        }
    }

    @Test
    public void validForm_submitsAndFinishesWithResultOk() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditActivity.class);

        try (ActivityScenario<EditActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Setting text directly via binding properties
                activity.binding.eventNameField.setText(R.string.dummy_event_name);
                activity.binding.eventAddressField.setText(R.string.dummy_event_address);
                activity.binding.eventDateField.setText("2026-10-18");

                activity.editEvent();

                assertTrue(activity.isFinishing(), "Activity should finish on valid submission");
            });
        }
    }
}