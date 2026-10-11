package edu.adam.eventtracker.eventrecyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.adam.eventtracker.MainActivity;
import edu.adam.eventtracker.databinding.EventListRowBinding;
import edu.adam.eventtracker.event.Event;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<Event> events;
    private final MainActivity mainAct;

    public EventAdapter(List<Event> eventList, MainActivity mainAct) {
        this.events = (eventList == null || eventList.isEmpty()) ? new ArrayList<>() : eventList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventListRowBinding rowBinding =
                EventListRowBinding
                        .inflate(LayoutInflater
                        .from(parent.getContext()), parent, false);
        rowBinding.getRoot().setOnClickListener(mainAct);
        rowBinding.getRoot().setOnLongClickListener(mainAct);
        return new EventViewHolder(rowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.binding.eventName.setText(event.getName());
        // Enum EventType has labels and a custom toString for UI formatting
        holder.binding.eventType.setText(event.getType().toString());
        holder.binding.eventRating.setText(event.getRating());
        holder.binding.eventDate.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }
}
