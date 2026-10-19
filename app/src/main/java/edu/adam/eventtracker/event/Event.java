package edu.adam.eventtracker.event;


import android.content.Context;
import android.content.res.Resources;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.adam.eventtracker.R;

public class Event implements Serializable {
    private String type;
    private String name;
    private String location;
    private String date;
    private String notes;
    private int rating;


    public Event(String type,
                 String name,
                 String location,
                 String date,
                 String notes,
                 int rating) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.notes = notes;
        setRating(rating);
        setType(type);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating <= 0) this.rating = 1;
        else if (rating > 5) this.rating = 5;
        else this.rating = rating;
    }

    // Ensures that the event type string comes out with the first letter capitalized
    public void setType(String eventType) {
        if (eventType != null && !eventType.isEmpty()) {
            this.type = Character.toUpperCase(eventType.charAt(0)) +
                    eventType.substring(1).toLowerCase();
        }
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }





}
