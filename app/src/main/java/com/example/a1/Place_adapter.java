package com.example.a1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Place_adapter extends RecyclerView.Adapter<Place_adapter.PlaceViewHolder> {

    private List<place_details> places;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(place_details item);
    }

    public Place_adapter(List<place_details> places, OnItemClickListener listener) {
        this.places = places != null ? new ArrayList<>(places) : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_view, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        place_details place = places.get(position);
        holder.placeTextView.setText(place.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void updateData(List<place_details> newPlaces) {
        this.places.clear();
        if (newPlaces != null) {
            this.places.addAll(newPlaces);
        }
        notifyDataSetChanged();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeTextView;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeTextView = itemView.findViewById(R.id.place_names);
        }
    }
}