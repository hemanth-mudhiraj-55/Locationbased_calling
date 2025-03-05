package com.example.a1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class place_adapter extends RecyclerView.Adapter<place_adapter.PlaceViewHolder> {

    private List<place_details> place_details_list;

    public place_adapter(List<place_details> place_details_list){
        this.place_details_list = place_details_list;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_view, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        place_details pd = place_details_list.get(position);
        holder.placeName.setText(pd.getDescription()); // ✅ Show place name
    }

    @Override
    public int getItemCount() {
        return place_details_list.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeName; // ✅ Add TextView reference

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.place_name); // ✅ Connect with XML
        }
    }
}
