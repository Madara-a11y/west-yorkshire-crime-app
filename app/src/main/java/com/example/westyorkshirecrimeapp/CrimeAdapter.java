package com.example.westyorkshirecrimeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.CrimeViewHolder> {

    private List<Crime> crimeList;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener; // Added for standard clicks

    public CrimeAdapter(List<Crime> crimeList) {
        this.crimeList = crimeList;
    }

    // --- NEW: Interface and Setter for standard clicks ---
    public interface OnItemClickListener {
        void onItemClick(Crime crime);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
    // -----------------------------------------------------

    public interface OnItemLongClickListener {
        void onItemLongClick(Crime crime);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull //never return null
    @Override
    public CrimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crime_item, parent, false);
        return new CrimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeViewHolder holder, int position) {
        Crime crime = crimeList.get(position);
        holder.type.setText(crime.crimeType);
        holder.location.setText(crime.location);
        holder.status.setText(crime.lastOutcome);

        // 1. Standard Click Listener (Fixes "nothing happens" issue)
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(crime);
            }
        });

        // 2. Existing Long Click Listener
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(crime);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return crimeList.size();
    }

    public void setFilteredList(List<Crime> filteredList) {
        this.crimeList = filteredList;
        notifyDataSetChanged();
    }

    public static class CrimeViewHolder extends RecyclerView.ViewHolder {
        TextView type, location, status;
        public CrimeViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.crimeType);
            location = itemView.findViewById(R.id.crimeLocation);
            status = itemView.findViewById(R.id.crimeStatus);
        }
    }
}