package edu.adam.eventtracker.eventrecyclerview;

import static androidx.core.content.ContextCompat.getString;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.adam.eventtracker.MainActivity;
import edu.adam.eventtracker.R;
import edu.adam.eventtracker.databinding.EventListRowBinding;
import edu.adam.eventtracker.event.Event;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    public List<Event> events;
    private final MainActivity mainAct;

    private boolean activeTypeFilter = false;
    private boolean activeRatingFilter = false;

    public EventAdapter(List<Event> eventList, MainActivity mainAct) {
        this.events = (eventList == null) ? new ArrayList<>() : new ArrayList<>(eventList);
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
        holder.binding.eventType.setText(event.getType());
        holder.binding.eventRating.setText(displayReviewStars(event.getRating()));
        holder.binding.eventDate.setText(event.getDate());
    }

    private String displayReviewStars(int rating) {
        switch (rating) {
            case 1:
                return mainAct.getString(R.string.star1);
            case 2:
                return mainAct.getString(R.string.star2);
            case 3:
                return mainAct.getString(R.string.star3);
            case 4:
                return mainAct.getString(R.string.star4);
            default:
                return mainAct.getString(R.string.star5);
        }
    }

    // Used for fully updating the adapter's collection or filtering
    private void resetAdapterList(List<Event> newList) {
        if (newList != null) {
            this.events.clear();
            this.events.addAll(newList);
            this.notifyDataSetChanged();
        }
    }

    /** Method specifically called by activity for completely resetting the filters. Passes original list */
    public void clearFilters(List<Event> ogList) {
        resetAdapterList(ogList);
    }

    public void filterByCategory(String cat) {
        ArrayList<Event> tempEvents = events.stream()
                .filter(e -> cat.equalsIgnoreCase(e.getType()))
                .collect(Collectors.toCollection(ArrayList::new));
        resetAdapterList(tempEvents);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }






}
