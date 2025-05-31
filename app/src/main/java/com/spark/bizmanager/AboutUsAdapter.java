package com.spark.bizmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AboutUsAdapter extends RecyclerView.Adapter<AboutUsAdapter.ViewHolder> {

    private Context context;
    private List<AboutUsModel> list;
    private DatabaseReference ref;

    public AboutUsAdapter(Context context, List<AboutUsModel> list) {
        this.context = context;
        this.list = list;
        this.ref = FirebaseDatabase.getInstance().getReference("AboutUs");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AboutUsModel model = list.get(position);

        holder.name.setText(model.getName());
        holder.position.setText(model.getPosition());
        holder.date.setText("Joined on: " + model.getDate());

        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(model.getImageUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.memberImage);
        } else {
            holder.memberImage.setImageResource(R.drawable.ic_image_placeholder);
        }

        holder.delete.setOnClickListener(v -> {
            int currentPosition = holder.getAbsoluteAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < list.size()) {
                AboutUsModel currentModel = list.get(currentPosition);
                String id = currentModel.getId();

                if (id != null && !id.isEmpty()) {
                    ref.child(id).removeValue()
                            .addOnSuccessListener(unused -> {
                                // Check again in case list changed during async operation
                                if (currentPosition < list.size() && list.get(currentPosition).getId().equals(id)) {
                                    list.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    notifyItemRangeChanged(currentPosition, list.size());
                                }
                                Toast.makeText(context, "Member deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, position, date;
        ImageView memberImage, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.memberName);
            position = itemView.findViewById(R.id.memberPosition);
            date = itemView.findViewById(R.id.memberDate);
            memberImage = itemView.findViewById(R.id.memberImg);
            delete = itemView.findViewById(R.id.deleteMember);
        }
    }
}