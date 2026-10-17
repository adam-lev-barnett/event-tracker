package edu.adam.eventtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.adam.eventtracker.databinding.ActivityMainBinding;
import edu.adam.eventtracker.event.Event;
import edu.adam.eventtracker.eventrecyclerview.EventAdapter;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private ActivityMainBinding binding;
    private final List<Event> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ActivityResultLauncher<Intent> arl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set main activity toolbar with menu - includes About activity and Create Event activity
        setUpRecycler();
        populateEventList();
        setUpCategorySpinner();
        setUpRatingSpinner();

        // Might need to set up last because recycler and spinner populate information the toolbar setup needs
        setUpToolbar();

        // To receive data back from navigating to new activity with Intent
        arl = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::handleIntentResult);
    }

    /** Sets the support action bar and updates the "subtitle" to display "event count" with list size */
    private void setUpToolbar() {
        setSupportActionBar(binding.mainActToolbar);
        updateToolbarEventCount();
    }

    // Continually updates event count when event is added, list is filtered, etc
    private void updateToolbarEventCount() {
        binding.mainActToolbar
                .setSubtitle(
                        getString(R.string.event_count, eventAdapter.getItemCount()));
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Separate method for cleaner code
    private void setUpRecycler() {
        // LOG //

        eventAdapter = new EventAdapter(eventList, this);
        binding.eventRecycler.setAdapter(eventAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.eventRecycler.setLayoutManager(linearLayoutManager);

    }

    private void populateEventList() {
        // Auto-returns new empty ArrayList if it couldn't read a file or if one didn't already exist
        List<Event> existingEvents = this.loadEvents();
        if (!existingEvents.isEmpty()) this.eventList.addAll(existingEvents);

            // If there is no data, use the dummy event data
        else populateRecyclerDummyData();

        eventAdapter.notifyDataSetChanged();
    }

    private void setUpCategorySpinner() {
        ArrayAdapter<CharSequence> catAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.event_cats,
                        android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(catAdapter);


        binding.categorySpinner.setSelection(0);

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Implement the event type filter; because it's only a temporary filter,
            // it made more sense to only change the adapter so we can keep a copy of the original data
            // and revert when the filter is cleared
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                applyFilters();
            }

        });

    }

    private void setUpRatingSpinner() {
        ArrayAdapter<CharSequence> rateAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.event_ratings,
                        android.R.layout.simple_spinner_item);
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ratingSpinner.setAdapter(rateAdapter);


        binding.ratingSpinner.setSelection(0);

        binding.ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Implement the event type filter; because it's only a temporary filter,
            // it made more sense to only change the adapter so we can keep a copy of the original data
            // and revert when the filter is cleared
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // We can use position as the int value to pass to the adapter to avoid string parsing
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Re-evaluates the filter status
                applyFilters();
            }

        });

    }

    private void applyFilters() {
        int catPos = binding.categorySpinner.getSelectedItemPosition();
        int selectedRating = binding.ratingSpinner.getSelectedItemPosition();

        String selectedCategory = (catPos > 0)
                ? binding.categorySpinner.getSelectedItem().toString()
                : null;

        List<Event> filtered = eventList.stream()
                .filter(e -> selectedCategory == null ||
                        selectedCategory.equalsIgnoreCase(e.getType()))
                .filter(e -> selectedRating == 0 ||
                        e.getRating() >= selectedRating)
                .collect(Collectors.toList());

        eventAdapter.setFilters(filtered);
        updateToolbarEventCount();
    }

    private List<Event> loadEvents() {
        try (InputStream input = getApplicationContext().openFileInput("Events.json")) {
            BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            if (sb.length() == 0) {
                Toast.makeText(this, R.string.no_file_content_exists, Toast.LENGTH_LONG).show();
                return new ArrayList<>();
            }

            Gson gson = new Gson();
            Event[] events = gson.fromJson(sb.toString(), Event[].class);
            List<Event> tempEventList = new ArrayList<>();
            Collections.addAll(tempEventList, events);
            return tempEventList;
        } catch (Exception e) {
            Toast.makeText(this, R.string.could_not_load_events, Toast.LENGTH_LONG).show();
            return new ArrayList<>();
        }
    }

    private void saveEventList() {
        try (FileOutputStream outputStream = getApplicationContext()
                .openFileOutput("Events.json", Context.MODE_PRIVATE)){
            PrintWriter printer = new PrintWriter(outputStream);
            printer.print(eventList.toString());
            printer.close();
        } catch (Exception e) {
            Toast.makeText(this, R.string.could_not_save_event_list, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        int pos = binding.eventRecycler.getChildLayoutPosition(v);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        Event toEdit = eventList.get(pos);
        intent.putExtra("EDIT_EVENT", toEdit);
        //TODO

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

    }

    private void handleIntentResult(ActivityResult res) {

    }

    // Testing recycler with dummy data
    private void populateRecyclerDummyData() {
        List<Event> events = Arrays.asList(
                new Event("CAFE", "Morning Coffee Meetup", "Blue Bottle Cafe", "2026-10-18", "Catching up with old friends", 4),
                new Event("PARK", "Weekend Picnic", "Millennium Park", "2026-10-19", "Enjoying the autumn weather", 5),
                new Event("CINEMA", "Movie Night", "AMC Downtown", "2026-10-20", "Watching the latest sci-fi release", 3),
                new Event("RESTAURANT", "Dinner with Colleagues", "Trattoria Roma", "2026-10-22", "Celebrating project launch", 5),
                new Event("CONCERT", "Rock Band Live", "City Arena", "2026-10-25", "Front row tickets", 4),
                new Event("MUSEUM", "Modern Art Exhibition", "Metropolitan Museum", "2026-10-27", "Exploring abstract paintings", 4),
                new Event("BAR", "Trivia Night", "The Local Pub", "2026-10-28", "Came in second place", 4),
                new Event("BEACH", "Sunset Walk", "South Beach", "2026-10-30", "Relaxing by the water", 5),
                new Event("THEATER", "Shakespearean Play", "State Playhouse", "2026-11-02", "Fantastic acting and stage design", 5),
                new Event("SPORTS", "Football Match", "City Stadium", "2026-11-05", "Home team won in overtime", 4)
        );
        this.eventList.addAll(events);
    }

    @Override
    public void onStop() {
        saveEventList();
        super.onStop();
    }


}