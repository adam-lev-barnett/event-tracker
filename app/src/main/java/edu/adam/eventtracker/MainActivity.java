package edu.adam.eventtracker;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.adam.eventtracker.databinding.ActivityMainBinding;
import edu.adam.eventtracker.event.Event;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
                return null;
            }

            Gson gson = new Gson();
            Event[] events = gson.fromJson(sb.toString(), Event[].class);
            List<Event> tempEventList = new ArrayList<>();
            Collections.addAll(tempEventList, events);
            return tempEventList;
        } catch (Exception e) {
            Toast.makeText(this, "Could not load events", Toast.LENGTH_LONG).show();
        }
        return null;
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
}