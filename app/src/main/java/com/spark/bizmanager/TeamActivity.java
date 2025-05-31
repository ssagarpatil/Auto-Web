package com.spark.bizmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TeamActivity extends AppCompatActivity implements TeamAdapter.OnTeamActionListener {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_CAMERA_PERMISSION = 103;

    RecyclerView rec1;
    ImageButton addspeci;
    ArrayList<TeamModel> list;
    TeamAdapter adapter;
    DatabaseReference dbRef;
    StorageReference storageRef;

    Uri imageUri;
    Uri photoUri;
    AlertDialog currentDialog;
    ProgressDialog progressDialog;
    TeamModel currentTeamForEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        rec1 = findViewById(R.id.rec1);
        addspeci = findViewById(R.id.addspeci);
        dbRef = FirebaseDatabase.getInstance().getReference("BestEmployee");
        storageRef = FirebaseStorage.getInstance().getReference("BestEmployee");

        list = new ArrayList<>();
        rec1.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeamAdapter(this, list, this);
        rec1.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait while we upload your data...");
        progressDialog.setCancelable(false);

        loadData();
        addspeci.setOnClickListener(view -> showAddDialog());
    }

    private void loadData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    TeamModel model = snap.getValue(TeamModel.class);
                    if (model != null) {
                        model.setKey(snap.getKey()); // Store the Firebase key
                        list.add(model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(TeamActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        showTeamDialog(null);
    }

    private void showEditDialog(TeamModel team) {
        currentTeamForEdit = team;
        showTeamDialog(team);
    }

    private void showTeamDialog(TeamModel team) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_team_add, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.imgPreview);
        EditText etEmployeeName = dialogView.findViewById(R.id.etName);
        EditText etAwardName = dialogView.findViewById(R.id.etAward);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        Button btnUpload = dialogView.findViewById(R.id.btnUpload);
        Button btnSelect = dialogView.findViewById(R.id.btnSelectImg);
        Button btnTakePhoto = dialogView.findViewById(R.id.btnTakePhoto);
        ImageButton btnRemoveImage = dialogView.findViewById(R.id.btnRemoveImg);

        if (team != null) {
            // Edit mode
            builder.setTitle("Edit Employee");
            btnUpload.setText("Update");
            etEmployeeName.setText(team.getEmployeeName());
            etAwardName.setText(team.getAwardName());
            etDate.setText(team.getDate());
            Glide.with(this).load(team.getImageUrl()).into(imageView);
            btnRemoveImage.setVisibility(View.VISIBLE);
        } else {
            // Add mode
            builder.setTitle("Add Employee");
            btnUpload.setText("Save");
            imageUri = null;
            photoUri = null;
            imageView.setImageResource(R.drawable.ic_image_placeholder);
            btnRemoveImage.setVisibility(View.GONE);
        }

        currentDialog = builder.create();
        currentDialog.show();

        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        btnSelect.setOnClickListener(v -> openGallery());
        btnTakePhoto.setOnClickListener(v -> checkCameraPermission());

        btnRemoveImage.setOnClickListener(v -> {
            imageUri = null;
            photoUri = null;
            imageView.setImageResource(R.drawable.ic_image_placeholder);
            btnRemoveImage.setVisibility(View.GONE);
        });

        btnUpload.setOnClickListener(v -> {
            String employeeName = etEmployeeName.getText().toString().trim();
            String awardName = etAwardName.getText().toString().trim();
            String awardDate = etDate.getText().toString().trim();

            if ((imageUri == null && photoUri == null && team == null) ||
                    employeeName.isEmpty() || awardName.isEmpty() || awardDate.isEmpty()) {
                Toast.makeText(this, "All fields and image are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (team != null) {
                // Editing existing team
                if (imageUri != null || photoUri != null) {
                    uploadImageAndUpdateTeam(imageUri != null ? imageUri : photoUri,
                            employeeName, awardName, awardDate, team.getKey());
                } else {
                    updateTeam(team.getKey(), employeeName, awardName, awardDate, team.getImageUrl());
                }
            } else {
                // Adding new team
                uploadImageToFirebase(imageUri != null ? imageUri : photoUri,
                        employeeName, awardName, awardDate);
            }
        });
    }

    private void showDatePicker(EditText etDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etDate.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else openCamera();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.spark.bizmanager.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(this, "File creation error", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadImageToFirebase(Uri uri, String name, String award, String date) {
        progressDialog.show();

        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");

        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long maxKey = 0;
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        try {
                                            long key = Long.parseLong(child.getKey());
                                            if (key > maxKey) maxKey = key;
                                        } catch (NumberFormatException e) {
                                            // ignore non-numeric keys
                                        }
                                    }
                                    long nextKey = maxKey + 1;
                                    TeamModel model = new TeamModel(name, award, date, downloadUri.toString());
                                    dbRef.child(String.valueOf(nextKey)).setValue(model).addOnCompleteListener(task -> {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(TeamActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                            if (currentDialog != null) currentDialog.dismiss();
                                        } else {
                                            Toast.makeText(TeamActivity.this, "Error saving", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(TeamActivity.this, "Failed to get max key", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndUpdateTeam(Uri uri, String name, String award, String date, String key) {
        progressDialog.show();

        // First delete old image if exists
        if (currentTeamForEdit != null && currentTeamForEdit.getImageUrl() != null) {
            StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentTeamForEdit.getImageUrl());
            oldImageRef.delete().addOnSuccessListener(aVoid -> {
                // After deleting old image, upload new one
                uploadNewImageForTeam(uri, name, award, date, key);
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Failed to delete old image", Toast.LENGTH_SHORT).show();
            });
        } else {
            // No old image to delete, just upload new one
            uploadNewImageForTeam(uri, name, award, date, key);
        }
    }

    private void uploadNewImageForTeam(Uri uri, String name, String award, String date, String key) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            updateTeam(key, name, award, date, downloadUri.toString());
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateTeam(String key, String name, String award, String date, String imageUrl) {
        TeamModel updatedTeam = new TeamModel(name, award, date, imageUrl);
        dbRef.child(key).setValue(updatedTeam)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        if (currentDialog != null) currentDialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error updating", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDelete(TeamModel team) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete this employee?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressDialog.show();
                    // Delete image from storage first
                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(team.getImageUrl());
                    imageRef.delete().addOnSuccessListener(aVoid -> {
                        // After image deleted, delete from database
                        dbRef.child(team.getKey()).removeValue().addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(TeamActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TeamActivity.this, "Error deleting", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(TeamActivity.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEdit(TeamModel team) {
        showEditDialog(team);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && currentDialog != null) {
            ImageView preview = currentDialog.findViewById(R.id.imgPreview);
            ImageButton btnRemove = currentDialog.findViewById(R.id.btnRemoveImg);

            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).centerCrop().into(preview);
                photoUri = null;
                btnRemove.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Glide.with(this).load(photoUri).centerCrop().into(preview);
                imageUri = null;
                btnRemove.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        currentDialog = null;
    }
}