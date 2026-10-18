package edu.adam.eventtracker.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.adam.eventtracker.MainActivity;
import edu.adam.eventtracker.event.Event;
import edu.adam.eventtracker.eventrecyclerview.EventAdapter;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventAdapterTest {

    private MainActivity activity;

    @BeforeEach
    public void setUp() {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void initialDataset_reportsCorrectItemCount() {
        List<Event> events = Arrays.asList(
                new Event("CAFE", "Meetup", "Cafe", "2026-10-18", "Note", 4),
                new Event("PARK", "Picnic", "Park", "2026-10-19", "Note", 5)
        );

        EventAdapter adapter = new EventAdapter(events, activity);
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void setFilters_swapsRenderedItems() {
        EventAdapter adapter = new EventAdapter(Collections.emptyList(), activity);
        assertEquals(0, adapter.getItemCount());

        List<Event> filtered = Collections.singletonList(
                new Event("PARK", "Picnic", "Park", "2026-10-19", "Note", 5)
        );

        adapter.setFilters(filtered);
        assertEquals(1, adapter.getItemCount());
    }
}