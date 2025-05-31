package com.spark.bizmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TestimonialAdapter extends RecyclerView.Adapter<TestimonialAdapter.ViewHolder> {

    private List<Testimonial> testimonialList;
    private DatabaseReference databaseReference;

    public interface OnTestimonialActionListener {
        void onDelete(Testimonial testimonial, int position);
    }

    private OnTestimonialActionListener actionListener;

    public TestimonialAdapter(List<Testimonial> testimonialList, OnTestimonialActionListener actionListener) {
        this.testimonialList = testimonialList;
        this.actionListener = actionListener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("response");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.testimonial_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Testimonial testimonial = testimonialList.get(position);
        holder.tvName.setText(testimonial.name);
        holder.tvEmail.setText(testimonial.email);
        holder.tvMessage.setText(testimonial.message);

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(testimonial, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return testimonialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvMessage;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}