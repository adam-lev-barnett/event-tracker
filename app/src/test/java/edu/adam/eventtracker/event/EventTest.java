package edu.adam.eventtracker.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void event_retainsAllConstructorProperties() {
        Event event = new Event("CAFE", "Morning Meetup", "Blue Bottle", "2026-10-18", "Chatting", 4);

        assertEquals("CAFE", event.getType());
        assertEquals("Morning Meetup", event.getName());
        assertEquals("Blue Bottle", event.getLocation());
        assertEquals("2026-10-18", event.getDate());
        assertEquals("Chatting", event.getNotes());
        assertEquals(4, event.getRating());
    }

    @Test
    public void event_updatesAttributesWhenEdited() {
        Event event = new Event("CAFE", "Morning Meetup", "Blue Bottle", "2026-10-18", "Chatting", 4);

        event.setName("Evening Coffee");
        event.setType("BAR");
        event.setRating(5);

        assertEquals("Evening Coffee", event.getName());
        assertEquals("BAR", event.getType());
        assertEquals(5, event.getRating());
    }
}