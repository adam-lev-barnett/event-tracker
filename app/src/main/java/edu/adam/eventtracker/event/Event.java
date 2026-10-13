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

    private static final Set<CharSequence> cats = new HashSet<>();

    public Event(String type,
                 String name,
                 String location,
                 String date,
                 String notes,
                 int rating) {
        this.type = type;
        this.name = name;
        this.location = location;
        this.date = date;
        this.notes = notes;
        setRating(rating);


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

    private void setRating(int rating) {
        if (rating <= 0) this.rating = 1;
        else if (rating > 5) this.rating = 5;
        else this.rating = rating;
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }





}
