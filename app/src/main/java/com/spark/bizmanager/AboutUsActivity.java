package com.spark.bizmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AboutUsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 101;

    private RecyclerView rec1;
    private ImageButton addSpeci;
    private List<AboutUsModel> list;
    private AboutUsAdapter adapter;
    private DatabaseReference ref;
    private StorageReference storageRef;

    private Uri imageUri, photoUri;
    private AlertDialog progressDialog;
    private AlertDialog currentDialog;

    private ImageView imagePreview;
    private ImageButton removeImage;

    // Define the sorting order for positions
    private static final Map<String, Integer> POSITION_PRIORITY = new HashMap<String, Integer>() {{
        put("Owner", 1);
        put("owner",1);
        put("CEO",2);
        put("Manager", 2);
        put("mananger",2);
        put("HR", 3);
        put("hr",3);
        put("Hr",3);
        // Add more positions as needed
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        rec1 = findViewById(R.id.rec1);
        addSpeci = findViewById(R.id.addspeci);
        list = new ArrayList<>();
        adapter = new AboutUsAdapter(this, list);

        ref = FirebaseDatabase.getInstance().getReference().child("AboutUs");
        storageRef = FirebaseStorage.getInstance().getReference().child("about_us_images");

        rec1.setLayoutManager(new LinearLayoutManager(this));
        rec1.setAdapter(adapter);

        fetchMembers();

        addSpeci.setOnClickListener(v -> openAddMemberDialog());
    }

    private void fetchMembers() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    AboutUsModel model = snap.getValue(AboutUsModel.class);
                    if (model != null) {
                        model.setId(snap.getKey());
                        list.add(model);
                    }
                }

                // Sort the list based on position hierarchy
                Collections.sort(list, new Comparator<AboutUsModel>() {
                    @Override
                    public int compare(AboutUsModel o1, AboutUsModel o2) {
                        int priority1 = POSITION_PRIORITY.getOrDefault(o1.getPosition(), Integer.MAX_VALUE);
                        int priority2 = POSITION_PRIORITY.getOrDefault(o2.getPosition(), Integer.MAX_VALUE);

                        // If same priority, sort by name
                        if (priority1 == priority2) {
                            return o1.getName().compareToIgnoreCase(o2.getName());
                        }
                        return Integer.compare(priority1, priority2);
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AboutUsActivity", "Database error: " + error.getMessage());
                Toast.makeText(AboutUsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_aboutus, null);
        builder.setView(view);

        EditText name = view.findViewById(R.id.editName);
        EditText position = view.findViewById(R.id.editPosition);
        EditText date = view.findViewById(R.id.editDate);
        imagePreview = view.findViewById(R.id.imagePreview);
        Button pickImage = view.findViewById(R.id.buttonPickImage);
        Button takePhoto = view.findViewById(R.id.buttonTakePhoto);
        removeImage = view.findViewById(R.id.btnRemoveImage);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Prevent keyboard from showing for the date field
        date.setInputType(InputType.TYPE_NULL);
        date.setFocusable(false);
        date.setClickable(true);

        imageUri = null;
        photoUri = null;
        imagePreview.setImageResource(R.drawable.ic_image_placeholder);
        removeImage.setVisibility(View.GONE);

        currentDialog = builder.create();
        currentDialog.show();

        // Set both click and focus listeners for better reliability
        date.setOnClickListener(v -> showDatePicker(date));
        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(date);
            }
        });

        pickImage.setOnClickListener(v -> openGallery());
        takePhoto.setOnClickListener(v -> checkCameraPermission());

        removeImage.setOnClickListener(v -> {
            imageUri = null;
            photoUri = null;
            imagePreview.setImageResource(R.drawable.ic_image_placeholder);
            removeImage.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            String memberName = name.getText().toString().trim();
            String memberPosition = position.getText().toString().trim();
            String memberDate = date.getText().toString().trim();
            Uri selectedUri = imageUri != null ? imageUri : photoUri;

            if (memberName.isEmpty() || memberPosition.isEmpty() || memberDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedUri == null) {
                saveMemberToFirebase(null, memberName, memberPosition, memberDate);
            } else {
                showProgressDialog();
                uploadImageToFirebase(selectedUri, memberName, memberPosition, memberDate);
            }
        });
    }

    private void showDatePicker(EditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    dateEditText.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showProgressDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
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
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.e("AboutUsActivity", "Error creating image file", e);
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadImageToFirebase(Uri uri, String name, String position, String date) {
        String id = ref.push().getKey();
        if (id == null) {
            dismissProgressDialog();
            Toast.makeText(this, "Could not generate ID", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileRef = storageRef.child(id + ".jpg");

        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            saveMemberToFirebase(downloadUri.toString(), name, position, date);
                        }))
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Log.e("AboutUsActivity", "Upload failed", e);
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveMemberToFirebase(String imageUrl, String name, String position, String date) {
        String id = ref.push().getKey();
        if (id == null) {
            dismissProgressDialog();
            Toast.makeText(this, "Could not generate ID", Toast.LENGTH_SHORT).show();
            return;
        }

        AboutUsModel model = new AboutUsModel(id, name, position, date,
                imageUrl != null ? imageUrl : "default_image_url");

        ref.child(id).setValue(model)
                .addOnCompleteListener(task -> {
                    dismissProgressDialog();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show();
                        if (currentDialog != null && currentDialog.isShowing()) {
                            currentDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(this, "Failed to save member", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Log.e("AboutUsActivity", "Save failed", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                if (imagePreview != null) {
                    imagePreview.setImageURI(imageUri);
                    if (removeImage != null) removeImage.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && photoUri != null) {
                if (imagePreview != null) {
                    imagePreview.setImageURI(photoUri);
                    if (removeImage != null) removeImage.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}