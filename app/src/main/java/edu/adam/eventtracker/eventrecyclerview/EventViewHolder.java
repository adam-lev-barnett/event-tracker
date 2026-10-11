package edu.adam.eventtracker.eventrecyclerview;

import androidx.recyclerview.widget.RecyclerView;

import edu.adam.eventtracker.databinding.EventListRowBinding;

public class EventViewHolder extends RecyclerView.ViewHolder{
    public final EventListRowBinding binding;

    public EventViewHolder(EventListRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
