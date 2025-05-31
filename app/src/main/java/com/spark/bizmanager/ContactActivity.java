//package com.spark.bizmanager;
//
//import android.app.TimePickerDialog;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.text.format.DateFormat;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textfield.TextInputEditText;
//
//import java.util.Calendar;
//
//public class ContactActivity extends AppCompatActivity {
//
//    private TextInputEditText etPhone, etWhatsApp, etEmail, etAddress, etTimings, etFacebook, etInstagram, etYouTube;
//    private MaterialButton btnSaveContact;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contact);
//
//        etPhone = findViewById(R.id.etPhone);
//        etWhatsApp = findViewById(R.id.etWhatsApp);
//        etEmail = findViewById(R.id.etEmail);
//        etAddress = findViewById(R.id.etAddress);
//        etTimings = findViewById(R.id.etTimings);
//        etFacebook = findViewById(R.id.etFacebook);
//        etInstagram = findViewById(R.id.etInstagram);
//        etYouTube = findViewById(R.id.etYouTube);
//        btnSaveContact = findViewById(R.id.btnSaveContact);
//
//        // Timings field non-editable but clickable
//        etTimings.setFocusable(false);
//        etTimings.setClickable(true);
//
//        // Show time pickers on timings field click
//        etTimings.setOnClickListener(v -> showTimePicker());
//
//        // Email auto-append @gmail.com on focus lost if no '@'
//        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
//                if (!email.isEmpty() && !email.contains("@")) {
//                    etEmail.setText(email + "@gmail.com");
//                }
//            }
//        });
//
//        // Email watcher to auto append @gmail.com after user types '@' with nothing after it
//        etEmail.addTextChangedListener(new TextWatcher() {
//            private boolean isEditing;
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // no action
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // no action
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (isEditing) return;
//
//                String text = s.toString();
//                int atIndex = text.indexOf("@");
//
//                if (atIndex != -1 && atIndex == text.length() - 1) {
//                    // user typed '@' at the end, auto append gmail.com
//                    isEditing = true;
//                    etEmail.setText(text + "gmail.com");
//                    etEmail.setSelection(etEmail.getText().length());
//                    isEditing = false;
//                }
//            }
//        });
//
//        // Limit phone and WhatsApp to digits only & max 10 digits
//        etPhone.addTextChangedListener(new TenDigitTextWatcher(etPhone));
//        etWhatsApp.addTextChangedListener(new TenDigitTextWatcher(etWhatsApp));
//
//        btnSaveContact.setOnClickListener(v -> saveContactInfo());
//    }
//
//    private void saveContactInfo() {
//        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
//        String whatsapp = etWhatsApp.getText() != null ? etWhatsApp.getText().toString().trim() : "";
//        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
//        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
//        String timings = etTimings.getText() != null ? etTimings.getText().toString().trim() : "";
//        String facebook = etFacebook.getText() != null ? etFacebook.getText().toString().trim() : "";
//        String instagram = etInstagram.getText() != null ? etInstagram.getText().toString().trim() : "";
//        String youtube = etYouTube.getText() != null ? etYouTube.getText().toString().trim() : "";
//
//        if (phone.length() != 10) {
//            etPhone.setError("Enter a valid 10-digit phone number");
//            etPhone.requestFocus();
//            return;
//        }
//
//        if (whatsapp.length() != 10) {
//            etWhatsApp.setError("Enter a valid 10-digit WhatsApp number");
//            etWhatsApp.requestFocus();
//            return;
//        }
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            etEmail.setError("Enter a valid email address");
//            etEmail.requestFocus();
//            return;
//        }
//
//        if (address.isEmpty()) {
//            etAddress.setError("Enter address");
//            etAddress.requestFocus();
//            return;
//        }
//
//        if (timings.isEmpty()) {
//            etTimings.setError("Select timings");
//            etTimings.requestFocus();
//            return;
//        }
//
//        // TODO: Validate Facebook, Instagram, YouTube links if needed (optional fields)
//
//        Toast.makeText(this, "Contact info saved successfully!", Toast.LENGTH_SHORT).show();
//
//        // Clear all fields after saving
//        clearAllFields();
//
//        // TODO: Save or send data to backend
//    }
//
//    private void clearAllFields() {
//        etPhone.setText("");
//        etWhatsApp.setText("");
//        etEmail.setText("");
//        etAddress.setText("");
//        etTimings.setText("");
//        etFacebook.setText("");
//        etInstagram.setText("");
//        etYouTube.setText("");
//    }
//
//    // Show start time picker, then end time picker sequentially
//    private void showTimePicker() {
//        final Calendar calendar = Calendar.getInstance();
//
//        // Show start time picker first
//        TimePickerDialog.OnTimeSetListener startTimeListener = (view, startHour, startMinute) -> {
//            Calendar startCal = Calendar.getInstance();
//            startCal.set(Calendar.HOUR_OF_DAY, startHour);
//            startCal.set(Calendar.MINUTE, startMinute);
//            String startTime = DateFormat.format("hh:mm a", startCal).toString();
//
//            // Then show end time picker
//            TimePickerDialog.OnTimeSetListener endTimeListener = (view2, endHour, endMinute) -> {
//                Calendar endCal = Calendar.getInstance();
//                endCal.set(Calendar.HOUR_OF_DAY, endHour);
//                endCal.set(Calendar.MINUTE, endMinute);
//                String endTime = DateFormat.format("hh:mm a", endCal).toString();
//
//                etTimings.setText(startTime + " - " + endTime);
//            };
//
//            new TimePickerDialog(ContactActivity.this, endTimeListener,
//                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
//        };
//
//        new TimePickerDialog(ContactActivity.this, startTimeListener,
//                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
//    }
//
//    // TextWatcher to allow only digits and max length 10
//    private static class TenDigitTextWatcher implements TextWatcher {
//        private final EditText editText;
//
//        public TenDigitTextWatcher(EditText editText) {
//            this.editText = editText;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // no action
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // no action
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            String input = s.toString();
//            // Remove non-digit chars
//            String filtered = input.replaceAll("[^0-9]", "");
//            if (!input.equals(filtered)) {
//                editText.setText(filtered);
//                editText.setSelection(filtered.length());
//                return;
//            }
//            // Limit length to 10
//            if (filtered.length() > 10) {
//                s.delete(10, filtered.length());
//            }
//        }
//    }
//}
//package com.spark.bizmanager;
//
//import android.app.TimePickerDialog;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.text.format.DateFormat;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ContactActivity extends AppCompatActivity {
//
//    private TextInputEditText etPhone, etWhatsApp, etEmail, etAddress, etTimings, etFacebook, etInstagram, etYouTube;
//    private MaterialButton btnSaveContact;
//
//    private DatabaseReference contactRef;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contact);
//
//        // Initialize Firebase reference
//        contactRef = FirebaseDatabase.getInstance().getReference("contacts");
//
//        etPhone = findViewById(R.id.etPhone);
//        etWhatsApp = findViewById(R.id.etWhatsApp);
//        etEmail = findViewById(R.id.etEmail);
//        etAddress = findViewById(R.id.etAddress);
//        etTimings = findViewById(R.id.etTimings);
//        etFacebook = findViewById(R.id.etFacebook);
//        etInstagram = findViewById(R.id.etInstagram);
//        etYouTube = findViewById(R.id.etYouTube);
//        btnSaveContact = findViewById(R.id.btnSaveContact);
//
//        etTimings.setFocusable(false);
//        etTimings.setClickable(true);
//        etTimings.setOnClickListener(v -> showTimePicker());
//
//        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
//                if (!email.isEmpty() && !email.contains("@")) {
//                    etEmail.setText(email + "@gmail.com");
//                }
//            }
//        });
//
//        etEmail.addTextChangedListener(new TextWatcher() {
//            private boolean isEditing;
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (isEditing) return;
//
//                String text = s.toString();
//                int atIndex = text.indexOf("@");
//
//                if (atIndex != -1 && atIndex == text.length() - 1) {
//                    isEditing = true;
//                    etEmail.setText(text + "gmail.com");
//                    etEmail.setSelection(etEmail.getText().length());
//                    isEditing = false;
//                }
//            }
//        });
//
//        etPhone.addTextChangedListener(new TenDigitTextWatcher(etPhone));
//        etWhatsApp.addTextChangedListener(new TenDigitTextWatcher(etWhatsApp));
//
//        btnSaveContact.setOnClickListener(v -> saveContactInfo());
//    }
//
//    private void saveContactInfo() {
//        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
//        String whatsapp = etWhatsApp.getText() != null ? etWhatsApp.getText().toString().trim() : "";
//        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
//        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
//        String timings = etTimings.getText() != null ? etTimings.getText().toString().trim() : "";
//        String facebook = etFacebook.getText() != null ? etFacebook.getText().toString().trim() : "";
//        String instagram = etInstagram.getText() != null ? etInstagram.getText().toString().trim() : "";
//        String youtube = etYouTube.getText() != null ? etYouTube.getText().toString().trim() : "";
//
//        if (phone.length() != 10) {
//            etPhone.setError("Enter a valid 10-digit phone number");
//            etPhone.requestFocus();
//            return;
//        }
//
//        if (whatsapp.length() != 10) {
//            etWhatsApp.setError("Enter a valid 10-digit WhatsApp number");
//            etWhatsApp.requestFocus();
//            return;
//        }
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            etEmail.setError("Enter a valid email address");
//            etEmail.requestFocus();
//            return;
//        }
//
//        if (address.isEmpty()) {
//            etAddress.setError("Enter address");
//            etAddress.requestFocus();
//            return;
//        }
//
//        if (timings.isEmpty()) {
//            etTimings.setError("Select timings");
//            etTimings.requestFocus();
//            return;
//        }
//
//        // Prepare data to save
//        String contactId = contactRef.push().getKey();
//        if (contactId == null) {
//            Toast.makeText(this, "Error generating contact ID", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Map<String, Object> contactMap = new HashMap<>();
//        contactMap.put("phone", phone);
//        contactMap.put("whatsapp", whatsapp);
//        contactMap.put("email", email);
//        contactMap.put("address", address);
//        contactMap.put("timings", timings);
//        contactMap.put("facebook", facebook);
//        contactMap.put("instagram", instagram);
//        contactMap.put("youtube", youtube);
//
//        contactRef.child(contactId).setValue(contactMap).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(ContactActivity.this, "Contact info saved successfully!", Toast.LENGTH_SHORT).show();
//                clearAllFields();
//            } else {
//                Toast.makeText(ContactActivity.this, "Failed to save contact info", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void clearAllFields() {
//        etPhone.setText("");
//        etWhatsApp.setText("");
//        etEmail.setText("");
//        etAddress.setText("");
//        etTimings.setText("");
//        etFacebook.setText("");
//        etInstagram.setText("");
//        etYouTube.setText("");
//    }
//
//    private void showTimePicker() {
//        final Calendar calendar = Calendar.getInstance();
//
//        TimePickerDialog.OnTimeSetListener startTimeListener = (view, startHour, startMinute) -> {
//            Calendar startCal = Calendar.getInstance();
//            startCal.set(Calendar.HOUR_OF_DAY, startHour);
//            startCal.set(Calendar.MINUTE, startMinute);
//            String startTime = DateFormat.format("hh:mm a", startCal).toString();
//
//            TimePickerDialog.OnTimeSetListener endTimeListener = (view2, endHour, endMinute) -> {
//                Calendar endCal = Calendar.getInstance();
//                endCal.set(Calendar.HOUR_OF_DAY, endHour);
//                endCal.set(Calendar.MINUTE, endMinute);
//                String endTime = DateFormat.format("hh:mm a", endCal).toString();
//
//                etTimings.setText(startTime + " - " + endTime);
//            };
//
//            new TimePickerDialog(ContactActivity.this, endTimeListener,
//                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
//        };
//
//        new TimePickerDialog(ContactActivity.this, startTimeListener,
//                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
//    }
//
//    private static class TenDigitTextWatcher implements TextWatcher {
//        private final EditText editText;
//
//        public TenDigitTextWatcher(EditText editText) {
//            this.editText = editText;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            String input = s.toString();
//            String filtered = input.replaceAll("[^0-9]", "");
//            if (!input.equals(filtered)) {
//                editText.setText(filtered);
//                editText.setSelection(filtered.length());
//                return;
//            }
//            if (filtered.length() > 10) {
//                s.delete(10, filtered.length());
//            }
//        }
//    }
//}


package com.spark.bizmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    EditText etPhone, etWhatsApp, etEmail, etAddress, etTimings, etFacebook, etInstagram, etYouTube;
    Button btnSave;

    DatabaseReference contactRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact); // Ensure this XML has all the required EditTexts and button

        // Initialize Firebase reference
        contactRef = FirebaseDatabase.getInstance().getReference("contacts").child("contactInfo");

        // Initialize views
        etPhone = findViewById(R.id.etPhone);
        etWhatsApp = findViewById(R.id.etWhatsApp);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etTimings = findViewById(R.id.etTimings);
        etFacebook = findViewById(R.id.etFacebook);
        etInstagram = findViewById(R.id.etInstagram);
        etYouTube = findViewById(R.id.etYouTube);
        btnSave = findViewById(R.id.btnSave);

        // Load existing contact data if any
        loadContactInfo();

        // Handle Save/Update click
        btnSave.setOnClickListener(v -> saveContactInfo());
    }

    private void loadContactInfo() {
        contactRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                etPhone.setText(snapshot.child("phone").getValue(String.class));
                etWhatsApp.setText(snapshot.child("whatsapp").getValue(String.class));
                etEmail.setText(snapshot.child("email").getValue(String.class));
                etAddress.setText(snapshot.child("address").getValue(String.class));
                etTimings.setText(snapshot.child("timings").getValue(String.class));
                etFacebook.setText(snapshot.child("facebook").getValue(String.class));
                etInstagram.setText(snapshot.child("instagram").getValue(String.class));
                etYouTube.setText(snapshot.child("youtube").getValue(String.class));
            }
        }).addOnFailureListener(e ->
                Toast.makeText(ContactActivity.this, "Failed to load contact info", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveContactInfo() {
        String phone = etPhone.getText().toString().trim();
        String whatsapp = etWhatsApp.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String timings = etTimings.getText().toString().trim();
        String facebook = etFacebook.getText().toString().trim();
        String instagram = etInstagram.getText().toString().trim();
        String youtube = etYouTube.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone is required");
            return;
        }

        Map<String, Object> contactMap = new HashMap<>();
        contactMap.put("phone", phone);
        contactMap.put("whatsapp", whatsapp);
        contactMap.put("email", email);
        contactMap.put("address", address);
        contactMap.put("timings", timings);
        contactMap.put("facebook", facebook);
        contactMap.put("instagram", instagram);
        contactMap.put("youtube", youtube);

        contactRef.setValue(contactMap).addOnSuccessListener(unused -> {
            Toast.makeText(ContactActivity.this, "Contact info saved", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ContactActivity.this, "Failed to save contact info", Toast.LENGTH_SHORT).show();
        });
    }
}
