package edu.adam.eventtracker.event;

import androidx.annotation.NonNull;

public enum EventType {
    BAR("Bar"),
    BEACH("Beach"),
    CAFE("Cafe"),
    CINEMA("Cinema"),
    CONCERT("Concert"),
    FESTIVAL("Festival"),
    GALLERY("Gallery"),
    HOTEL("Hotel"),
    MUSEUM("Museum"),
    OTHER("Other"),
    PARK("Park"),
    RESTAURANT("Restaurant"),
    SPORTS("Sports"),
    THEATER("Theater");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    @NonNull
    @Override
    public String toString() {
        return this.label;
    }
}
