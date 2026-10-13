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
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set main activity toolbar with menu - includes About activity and Create Event activity
        setSupportActionBar(binding.mainActToolbar);
        setUpRecycler();

        // Auto-returns new empty ArrayList if it couldn't read a file or if one didn't already exist
        this.eventList.addAll(this.loadEvents());

        // To receive data back from navigating to new activity with Intent
        arl = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::handleIntentResult);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Separate method for cleaner code
    private void setUpRecycler() {
        eventAdapter = new EventAdapter(eventList, this);
        binding.eventRecycler.setAdapter(eventAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.eventRecycler.setLayoutManager(linearLayoutManager);
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

                if (position == 0) {
                    resetFilter();
                    return;
                }
                Optional<CharSequence> catStringOpt = Optional.ofNullable(catAdapter.getItem(position));
                if (catStringOpt.isPresent()) {
                    String catString = catStringOpt.get().toString();
                    ArrayList<Event> tempEvents = eventList.stream()
                            .filter(e -> catString.equals(e.getType()))
                            .collect(Collectors.toCollection(ArrayList::new));
                    eventAdapter.events.clear();
                    eventAdapter.events.addAll(tempEvents);
                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resetFilter();
            }

            private void resetFilter() {
                eventAdapter.events.clear();
                eventAdapter.events.addAll(eventList);
                eventAdapter.notifyDataSetChanged();
            }
        });

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
                Toast.makeText(this, "No file content could exists", Toast.LENGTH_LONG).show();
                return new ArrayList<>();
            }

            Gson gson = new Gson();
            Event[] events = gson.fromJson(sb.toString(), Event[].class);
            List<Event> tempEventList = new ArrayList<>();
            Collections.addAll(tempEventList, events);
            return tempEventList;
        } catch (Exception e) {
            Toast.makeText(this, "Could not load events", Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }

    private void saveEventList() {
        try (FileOutputStream outputStream = getApplicationContext()
                .openFileOutput("Events.json", Context.MODE_PRIVATE)){
            PrintWriter printer = new PrintWriter(outputStream);
            printer.print(eventList.toString());
            printer.close();
        } catch (Exception e) {
            Toast.makeText(this, "Could not load event list", Toast.LENGTH_LONG).show();
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


}