package com.spark.bizmanager;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestimonialsActivity extends AppCompatActivity
        implements TestimonialAdapter.OnTestimonialActionListener {

    private RecyclerView recyclerView;
    private TestimonialAdapter adapter;
    private List<Testimonial> testimonialList;
    private DatabaseReference responseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials);

        recyclerView = findViewById(R.id.recyclerView);
        testimonialList = new ArrayList<>();
        adapter = new TestimonialAdapter(testimonialList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Firebase path: responses
        responseRef = FirebaseDatabase.getInstance().getReference("response");

        loadTestimonials();
    }

    private void loadTestimonials() {
        responseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testimonialList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Testimonial testimonial = dataSnapshot.getValue(Testimonial.class);
                    if (testimonial != null) {
                        testimonial.setKey(dataSnapshot.getKey()); // Store the Firebase key
                        testimonialList.add(testimonial);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TestimonialsActivity.this,
                        "Failed to load testimonials: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDelete(Testimonial testimonial, int position) {
        if (testimonial.getKey() != null) {
            responseRef.child(testimonial.getKey()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Testimonial deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Error: Couldn't find testimonial to delete",
                    Toast.LENGTH_SHORT).show();
        }
    }
}