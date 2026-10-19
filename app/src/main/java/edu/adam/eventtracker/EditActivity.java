package edu.adam.eventtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import edu.adam.eventtracker.databinding.ActivityEditBinding;
import edu.adam.eventtracker.event.Event;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    Event event;
    static final String TAG = "EditActivity";
    Intent intent;


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

        getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                this::backInvoked);

        intent = getIntent();
        if (intent != null) {
            event = intent.getSerializableExtra(getString(R.string.edit_event), Event.class);
            populateFields();
        }
        else intent = new Intent();
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

        // This doesn't need to do anything because all it does is select the item
        binding.editEventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setUpEventStarsSpinner() {
        ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter
                .createFromResource(this, R.array.event_ratings, android.R.layout.simple_spinner_item);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.editEventRatingSpinner.setAdapter(eventTypeAdapter);

        binding.editEventRatingSpinner.setSelection(0);

        // This doesn't need to do anything because all it does is select the item
        binding.editEventRatingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Technically only one item, so can default to on-click. But for the sake of extensibility
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_icon) submitEvent();
        return super.onOptionsItemSelected(item);
    }

    /** Populates existing fields on loading if editing an existing event */
    private void populateFields() {
        // If it can't deserialize
        if (event != null) {
            // Maintain event's position in list
            int pos = intent.getIntExtra("INDEX", -1);
            // If there's no string resource of that name, we can't return to a random index, so we exit
            if (pos == -1) return;

            binding.eventNameField.setText(event.getName());
            binding.eventAddressField.setText(event.getLocation());
            binding.eventDateField.setText(event.getDate());
            binding.eventNotesField.setText(event.getNotes());
            binding.editEventRatingSpinner.setSelection(event.getRating());

            int typePosition = getEventTypeSpinnerIndex();
            // handles any accidental call for getEventTypeSpinner
            binding.editEventTypeSpinner.setSelection(Math.max(typePosition, 1));
        }
    }

    private int getEventTypeSpinnerIndex() {
        String[] eventTypes = getResources().getStringArray(R.array.event_cats);
        // Skips default spinner value
        for (int i = 1; i < eventTypes.length; i++) {
            if (eventTypes[i].equals(event.getType())) return i;
        }
        return -1;
    }

    public void backInvoked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save);
        builder.setMessage(R.string.save_event_prompt);
        builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> submitEvent());
        builder.setNegativeButton(R.string.no, (dialog, id) -> finish());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void submitEvent() {
        String name = binding.eventNameField.getText().toString().trim();
        String date = binding.eventDateField.getText().toString().trim();
        String address = binding.eventAddressField.getText().toString().trim();
        String type = binding.editEventTypeSpinner.getSelectedItem().toString();
        int typePos = binding.editEventTypeSpinner.getSelectedItemPosition();
        int ratingPos = binding.editEventRatingSpinner.getSelectedItemPosition();

        if (!validateAndHighlightForm(name, date, address, type, typePos, ratingPos)) return;

        String notes = binding.eventNotesField.getText().toString().trim();

        // a new event won't have a position from the recyclerview yet
        boolean isNewEvent = !intent.hasExtra("INDEX");

        if (!isNewEvent) {
            event.setRating(ratingPos);
            event.setName(name);
            event.setDate(date);
            event.setLocation(address);
            event.setType(type);
            event.setNotes(notes);
        }
        else {
            this.event = new Event(type, name, address, date, notes, ratingPos);
        }

        Intent returnData = new Intent();
        returnData.putExtra(getString(R.string.is_new_event), isNewEvent);
        returnData.putExtra(getString(R.string.edit_event), this.event);
        // Re-extract original event list position
        if (!isNewEvent) returnData.putExtra(getString(R.string.INDEX), intent.getIntExtra("INDEX", -1));
        setResult(RESULT_OK, returnData);
        finish();
    }

    private boolean validateAndHighlightForm(String name, String date, String address, String type, int typePos, int ratingPos) {
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.eventNameField.setBackgroundResource(R.drawable.error_edit_text_border);
            isValid = false;
        } else {
            binding.eventNameField.setBackgroundResource(R.drawable.normal_edit_text_border);
        }

        if (!validateDate(date)) {
            binding.eventDateField.setBackgroundResource(R.drawable.error_edit_text_border);
            isValid = false;
        } else {
            binding.eventDateField.setBackgroundResource(R.drawable.normal_edit_text_border);
        }

        if (address.isEmpty()) {
            binding.eventAddressField.setBackgroundResource(R.drawable.error_edit_text_border);
            isValid = false;
        } else {
            binding.eventAddressField.setBackgroundResource(R.drawable.normal_edit_text_border);
        }

        if (type.isEmpty() || typePos <= 0) {
            binding.editEventTypeSpinner.setBackgroundResource(R.drawable.error_edit_text_border);
            isValid = false;
        } else {
            binding.editEventTypeSpinner.setBackgroundResource(R.drawable.normal_edit_text_border);
        }

        if (ratingPos <= 0) {
            binding.editEventRatingSpinner.setBackgroundResource(R.drawable.error_edit_text_border);
            isValid = false;
        } else {
            binding.editEventRatingSpinner.setBackgroundResource(R.drawable.normal_edit_text_border);
        }

        if (!isValid) {
            Toast.makeText(this,
                    R.string.please_fill_out_the_requested_fields, Toast.LENGTH_LONG).show();
        }
        return isValid;
    }



    private boolean validateDate(String date) {
        Pattern pattern = Pattern.compile(getString(R.string.date_format));

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(getString(R.string.date_time_parse_format))
                .withResolverStyle(ResolverStyle.STRICT);

        if (date == null || !pattern.matcher(date.trim()).matches()) return false;

        try {
            LocalDate.parse(date.trim(), formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }



}