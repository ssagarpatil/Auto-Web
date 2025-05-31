package com.spark.bizmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private Context context;
    private List<TeamModel> teamList;
    private DatabaseReference dbRef;
    private StorageReference storageRef;

    public interface OnTeamActionListener {
        void onDelete(TeamModel team);
        void onEdit(TeamModel team);
    }

    private OnTeamActionListener actionListener;

    public TeamAdapter(Context context, List<TeamModel> teamList, OnTeamActionListener actionListener) {
        this.context = context;
        this.teamList = teamList;
        this.actionListener = actionListener;
        this.dbRef = FirebaseDatabase.getInstance().getReference("BestEmployee");
        this.storageRef = FirebaseStorage.getInstance().getReference("BestEmployee");
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        TeamModel model = teamList.get(position);
        holder.name.setText(model.getEmployeeName());
        holder.award.setText(model.getAwardName());
        holder.date.setText(model.getDate());
        Glide.with(context).load(model.getImageUrl()).into(holder.image);

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(model);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView name, award, date;
        ImageView image;
        ImageButton btnDelete, btnEdit;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvEmployeeName);
            award = itemView.findViewById(R.id.tvAwardName);
            date = itemView.findViewById(R.id.tvAwardDate);
            image = itemView.findViewById(R.id.imageViewEmployee);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}