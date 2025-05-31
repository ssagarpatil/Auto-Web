package com.spark.bizmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final String TAG = "MainActivity";

    private EditText etBusinessName, etAboutBusiness;
    private ImageView imgLogo;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_BUSINESS_NAME = "business_name";
    private static final String KEY_ABOUT_BUSINESS = "about_business"; // Added separate key

    private Uri logoUri; // Uri of selected image

    // Firebase references
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // Card and status ImageViews
    private MaterialCardView cardSpecialties, cardProducts, cardTeam, cardGallery, cardTestimonials, cardContact, cardAbout;
    private ImageView ivSpecialtiesStatus, ivProductsStatus, ivTeamStatus, ivGalleryStatus, ivTestimonialsStatus, ivContactStatus, ivAboutStatus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        etBusinessName = findViewById(R.id.etBusinessName);
        imgLogo = findViewById(R.id.imgLogo);
        etAboutBusiness = findViewById(R.id.etAboutBusiness);

        // Initialize card and status ImageViews
        cardSpecialties = findViewById(R.id.cardSpecialties);
        cardProducts = findViewById(R.id.cardProducts);
        cardTeam = findViewById(R.id.cardTeam);
        cardGallery = findViewById(R.id.cardGallery);
        cardTestimonials = findViewById(R.id.cardTestimonials);
        cardContact = findViewById(R.id.cardContact);
        cardAbout = findViewById(R.id.cardAbout);

        ivSpecialtiesStatus = findViewById(R.id.ivSpecialtiesStatus);
        ivProductsStatus = findViewById(R.id.ivProductsStatus);
        ivTeamStatus = findViewById(R.id.ivTeamStatus);
        ivGalleryStatus = findViewById(R.id.ivGalleryStatus);
        ivTestimonialsStatus = findViewById(R.id.ivTestimonialsStatus);
        ivContactStatus = findViewById(R.id.ivContactStatus);
        ivAboutStatus = findViewById(R.id.ivAboutStatus);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("businessLogos");

        // Load business info and check all Firebase nodes
        loadBusinessInfoAndCheckNodes();

        // Load locally saved data from SharedPreferences (optional backup)
        String savedName = sharedPreferences.getString(KEY_BUSINESS_NAME, "");
        String savedAbout = sharedPreferences.getString(KEY_ABOUT_BUSINESS, "");

        if (etBusinessName.getText().toString().isEmpty() && !savedName.isEmpty()) {
            etBusinessName.setText(savedName);
        }

        if (etAboutBusiness.getText().toString().isEmpty() && !savedAbout.isEmpty()) {
            etAboutBusiness.setText(savedAbout);
        }

        // TextWatcher to save business name locally and update Firebase
        etBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString();

                // Save locally
                sharedPreferences.edit().putString(KEY_BUSINESS_NAME, name).apply();

                // Save to Firebase
                databaseReference.child("businessInfo").child("businessName").setValue(name);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // TextWatcher to save about business locally and update Firebase
        etAboutBusiness.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String aboutText = s.toString();

                // Save locally with correct key
                sharedPreferences.edit().putString(KEY_ABOUT_BUSINESS, aboutText).apply();

                // Save to Firebase
                databaseReference.child("businessInfo").child("AboutBusiness").setValue(aboutText);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Logo ImageView click: open gallery to select image
        imgLogo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Navigation click listeners for cards
        cardSpecialties.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SpecialtiesActivity.class)));
        cardProducts.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProductsActivity.class)));
        cardTeam.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TeamActivity.class)));
        cardGallery.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GalleryActivity.class)));
        cardTestimonials.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TestimonialsActivity.class)));
        cardContact.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactActivity.class)));
        cardAbout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutUsActivity.class)));

        // Set up real-time listeners for all Firebase nodes
        setupFirebaseListeners();
    }

    private void loadBusinessInfoAndCheckNodes() {
        databaseReference.child("businessInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load business name
                    if (snapshot.hasChild("businessName")) {
                        String name = snapshot.child("businessName").getValue(String.class);
                        if (name != null && !name.isEmpty()) {
                            etBusinessName.setText(name);
                        }
                    }

                    // Load about business - FIXED: Now loading the correct field
                    if (snapshot.hasChild("AboutBusiness")) {
                        String aboutBusiness = snapshot.child("AboutBusiness").getValue(String.class);
                        if (aboutBusiness != null && !aboutBusiness.isEmpty()) {
                            etAboutBusiness.setText(aboutBusiness);
                        }
                    }

                    // Load business logo
                    if (snapshot.hasChild("businessLogo")) {
                        String logoUrl = snapshot.child("businessLogo").getValue(String.class);
                        if (logoUrl != null && !logoUrl.isEmpty()) {
                            Glide.with(MainActivity.this)
                                    .load(logoUrl)
                                    .placeholder(R.drawable.placeholder)
                                    .into(imgLogo);
                        }
                    }
                }

                // Check all Firebase nodes after loading business info
                checkAllFirebaseNodes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load business info", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load business info: " + error.getMessage());
                // Still check nodes even if business info fails
                checkAllFirebaseNodes();
            }
        });
    }

    private void setupFirebaseListeners() {
        // Listen for changes in specialties node
        databaseReference.child("specialties").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardSpecialties, ivSpecialtiesStatus, hasData, "Specialties");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to listen to specialties: " + error.getMessage());
                updateIndicator(cardSpecialties, ivSpecialtiesStatus, false, "Specialties");
            }
        });

        // Listen for changes in products node
        databaseReference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardProducts, ivProductsStatus, hasData, "Products");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to listen to products: " + error.getMessage());
                updateIndicator(cardProducts, ivProductsStatus, false, "Products");
            }
        });

        // Listen for changes in BestEmployee node (Team)
        databaseReference.child("BestEmployee").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardTeam, ivTeamStatus, hasData, "Team");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to listen to BestEmployee: " + error.getMessage());
                updateIndicator(cardTeam, ivTeamStatus, false, "Team");
            }
        });

        // Listen for changes in images node (Gallery)
        databaseReference.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardGallery, ivGalleryStatus, hasData, "Gallery");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to listen to images: " + error.getMessage());
                updateIndicator(cardGallery, ivGalleryStatus, false, "Gallery");
            }
        });

        // Listen for changes in response node (Testimonials)
        databaseReference.child("response").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardTestimonials, ivTestimonialsStatus, hasData, "Testimonials");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to listen to response: " + error.getMessage());
                updateIndicator(cardTestimonials, ivTestimonialsStatus, false, "Testimonials");
            }
        });

        // Listen for changes in contacts node
        databaseReference.child("contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardContact, ivContactStatus, hasData, "Contact");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to listen to contacts: " + error.getMessage());
                updateIndicator(cardContact, ivContactStatus, false, "Contact");
            }
        });

        // Listen for changes in AboutUs node
        databaseReference.child("AboutUs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardAbout, ivAboutStatus, hasData, "About Us");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to listen to AboutUs: " + error.getMessage());
                updateIndicator(cardAbout, ivAboutStatus, false, "About Us");
            }
        });
    }

    private void checkAllFirebaseNodes() {
        // Check specialties node
        databaseReference.child("specialties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardSpecialties, ivSpecialtiesStatus, hasData, "Specialties");
                Log.d(TAG, "Specialties check: " + hasData + " (children: " + snapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check specialties: " + error.getMessage());
                updateIndicator(cardSpecialties, ivSpecialtiesStatus, false, "Specialties");
            }
        });

        // Check products node
        databaseReference.child("products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardProducts, ivProductsStatus, hasData, "Products");
                Log.d(TAG, "Products check: " + hasData + " (children: " + snapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check products: " + error.getMessage());
                updateIndicator(cardProducts, ivProductsStatus, false, "Products");
            }
        });

        // Check BestEmployee node (Team)
        databaseReference.child("BestEmployee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardTeam, ivTeamStatus, hasData, "Team");
                Log.d(TAG, "BestEmployee check: " + hasData + " (children: " + snapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check BestEmployee: " + error.getMessage());
                updateIndicator(cardTeam, ivTeamStatus, false, "Team");
            }
        });

        // Check images node (Gallery)
        databaseReference.child("images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardGallery, ivGalleryStatus, hasData, "Gallery");
                Log.d(TAG, "Images check: " + hasData + " (children: " + snapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check images: " + error.getMessage());
                updateIndicator(cardGallery, ivGalleryStatus, false, "Gallery");
            }
        });

        // Check response node (Testimonials)
        databaseReference.child("response").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardTestimonials, ivTestimonialsStatus, hasData, "Testimonials");
                Log.d(TAG, "Response check: " + hasData + " (children: " + snapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check response: " + error.getMessage());
                updateIndicator(cardTestimonials, ivTestimonialsStatus, false, "Testimonials");
            }
        });

        // Check contacts node
        databaseReference.child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardContact, ivContactStatus, hasData, "Contact");
                Log.d(TAG, "Contacts check: " + hasData + " (children: " + snapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check contacts: " + error.getMessage());
                updateIndicator(cardContact, ivContactStatus, false, "Contact");
            }
        });

        // Check AboutUs node
        databaseReference.child("AboutUs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasData = snapshot.exists() && snapshot.getChildrenCount() > 0;
                updateIndicator(cardAbout, ivAboutStatus, hasData, "About Us");
                Log.d(TAG, "AboutUs check: " + hasData + " (children: " + snapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check AboutUs: " + error.getMessage());
                updateIndicator(cardAbout, ivAboutStatus, false, "About Us");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            logoUri = data.getData();

            // Display selected image immediately
            imgLogo.setImageURI(logoUri);

            // Upload to Firebase Storage
            uploadLogoToFirebase(logoUri);
        }
    }

    private void uploadLogoToFirebase(Uri fileUri) {
        if (fileUri == null) return;

        // Use a fixed file name or unique id - here we use "logo.jpg"
        StorageReference logoRef = storageReference.child("logo.jpg");

        logoRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();

                    // Save download URL to Realtime Database under businessInfo/businessLogo
                    databaseReference.child("businessInfo").child("businessLogo").setValue(downloadUrl);

                    Toast.makeText(MainActivity.this, "Logo uploaded successfully", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to upload logo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Logo upload failed", e);
                });
    }

    private void updateIndicator(MaterialCardView card, ImageView statusIcon, boolean hasData, String sectionName) {
        if (statusIcon == null) {
            Log.w(TAG, "Skipping update for " + sectionName + ": statusIcon is null");
            return;
        }

        runOnUiThread(() -> {
            if (hasData) {
                statusIcon.setImageResource(R.drawable.check);
                statusIcon.setVisibility(View.VISIBLE);
                // Optional: set a subtle green background or tint
//                statusIcon.setBackgroundColor(Color.parseColor("#2200FF00")); // Light green transparent
                Log.d(TAG, sectionName + ": Showing green checkmark (has data)");
            } else {
                statusIcon.setImageResource(R.drawable.check1);
                statusIcon.setVisibility(View.VISIBLE);
                // Optional: set a subtle red background or tint
//                statusIcon.setBackgroundColor(Color.parseColor("#22FF0000")); // Light red transparent
                Log.d(TAG, sectionName + ": Showing red error icon (no data)");
            }
        });

        // Refresh layout if needed
        View rootView = findViewById(R.id.rootLinearLayout);
        if (rootView != null) {
            rootView.post(() -> rootView.invalidate());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh indicators when returning to this activity
        checkAllFirebaseNodes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any listeners if needed
        // Firebase listeners are automatically cleaned up when activity is destroyed
    }
}