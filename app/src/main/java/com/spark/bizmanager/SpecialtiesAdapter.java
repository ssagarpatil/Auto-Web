package com.spark.bizmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SpecialtiesAdapter extends RecyclerView.Adapter<SpecialtiesAdapter.SpecialtyViewHolder> {

    private ArrayList<String> specialtiesList;
    private ArrayList<String> specialtiesKeys;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(String key, String currentValue);
        void onDeleteClick(String key);
    }

    public SpecialtiesAdapter(ArrayList<String> specialtiesList, ArrayList<String> specialtiesKeys, OnItemClickListener listener) {
        this.specialtiesList = specialtiesList;
        this.specialtiesKeys = specialtiesKeys;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_specialty, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        String specialty = specialtiesList.get(position);
        String key = specialtiesKeys.get(position);
        holder.bind(specialty, key);
    }

    @Override
    public int getItemCount() {
        return specialtiesList.size();
    }

    class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        TextView specialtyText;
        ImageButton editButton, deleteButton;

        SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            specialtyText = itemView.findViewById(R.id.specialtyText);
            editButton = itemView.findViewById(R.id.editBtn);
            deleteButton = itemView.findViewById(R.id.deleteBtn);
        }

        void bind(String specialty, String key) {
            specialtyText.setText(specialty);
            editButton.setOnClickListener(v -> listener.onEditClick(key, specialty));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(key));
        }
    }
}
