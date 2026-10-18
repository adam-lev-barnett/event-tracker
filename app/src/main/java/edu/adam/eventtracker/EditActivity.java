package edu.adam.eventtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.adam.eventtracker.databinding.ActivityEditBinding;
import edu.adam.eventtracker.event.Event;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;
    private Event event;
    String TAG = getString(R.string.edit_activity);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.editActToolbar);
        setUpEventTypeSpinner();
        setUpEventStarsSpinner();

        Intent intent = new Intent();
        event = intent.getSerializableExtra(getString(R.string.edit_event), Event.class);

        // Will finish with a cancel status if null checks, etc. fail
        populateFields(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    private void setUpEventTypeSpinner() {
        ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter
                .createFromResource(this, R.array.event_cats, android.R.layout.simple_spinner_item);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.editEventTypeSpinner.setAdapter(eventTypeAdapter);

        binding.editEventTypeSpinner.setSelection(0);
        binding.editEventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpEventStarsSpinner() {
        ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter
                .createFromResource(this, R.array.event_ratings, android.R.layout.simple_spinner_item);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.editEventTypeSpinner.setAdapter(eventTypeAdapter);

        binding.editEventTypeSpinner.setSelection(0);
        binding.editEventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Technically only one item, so can default to on-click. But for the sake of extensibility
    //TODO make clickable
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    /** Populates existing fields on loading if editing an existing event */
    private void populateFields(Intent intent) {

        // If it can't deserialize
        if (event != null) {
            // Maintain event's position in list
            int pos = intent.getIntExtra(getString((R.string.INDEX)), -1);

            // If there's no string resource of that name, we can't return to a random index, so we exit
            if (pos == -1) {
                Log.d(getString(R.string.edit_activity), getString(R.string.invalid_edit_position));
                setResult(RESULT_CANCELED, intent);
                finish();
                return;
            }

            binding.eventNameField.setText(event.getName());
            binding.eventAddressField.setText(event.getLocation());
            binding.eventDateField.setText(event.getDate());
            binding.notesField.setText(event.getNotes());
        }

    }

    public void createEvent() {

    }



}