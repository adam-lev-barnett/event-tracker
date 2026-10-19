package edu.adam.eventtracker.eventrecyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.adam.eventtracker.MainActivity;
import edu.adam.eventtracker.R;
import edu.adam.eventtracker.databinding.EventListRowBinding;
import edu.adam.eventtracker.event.Event;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    public List<Event> events;
    private final MainActivity mainAct;
    public static String TAG = "EventAdapter";



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

        String[] eventStars = mainAct.getResources().getStringArray(R.array.event_ratings);
        return eventStars[rating];
    }

    // Used for fully updating the adapter's collection or filtering
    private void modifyAdapterList(List<Event> newList) {
        if (newList != null) {
            this.events.clear();
            this.events.addAll(newList);
            this.notifyDataSetChanged();
        }
    }

    public void removeEvent(int i) {
        if (i >= 0 && i < events.size()) events.remove(i);
        else {
            Toast.makeText(mainAct, R.string.could_not_remove_item, Toast.LENGTH_LONG).show();
            Log.d(TAG, mainAct.getString(R.string.invalid_index_for_event_removal) + ": " + i);
        }
        notifyItemRemoved(i);
    }

    public void loadEvents(List<Event> eventList) {
        events.clear();
        events.addAll(eventList);
        notifyDataSetChanged();
    }

    public void addEventAtTop(Event event) {
        if (event != null) {
            this.events.add(0, event);
            notifyItemInserted(0);
        }
    }

    public void updateEventAt(int position, Event event) {
        if (event != null && position >= 0 && position < this.events.size()) {
            this.events.set(position, event);
            notifyItemChanged(position);
        }
    }

    /** Method specifically called by activity for completely resetting the filters. Passes original list */
    public void setFilters(List<Event> ogList) {
        modifyAdapterList(ogList);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }






}
