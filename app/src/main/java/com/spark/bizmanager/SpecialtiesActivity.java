package com.spark.bizmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class SpecialtiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton btnGenerate, addSpeci;
    private ArrayList<String> specialtiesList;
    private ArrayList<String> specialtiesKeys;
    private SpecialtiesAdapter adapter;
    private DatabaseReference specialtiesRef;

    private final String[] predefinedSpecialties = new String[] {
            "Artisan ironwork since 1923", "Triple-inspected luxury timepieces",
            "Small-batch organic juices", "Sommelier-curated wine selections",
            "Surgical-grade stainless cookware", "One-hour emergency plumbing",
            "15-minute pizza delivery", "Same-day dry cleaning", "24/7 mobile notary",
            "Instant prescription renewals", "Zero-waste packaging solutions",
            "Carbon-neutral shipping worldwide", "100% recycled footwear",
            "Solar-powered manufacturing", "Fair-trade coffee beans",
            "AI-powered fitness coaching", "Blockchain-verified authenticity",
            "3D-printed architectural models", "Voice-controlled appliances",
            "AR makeup trials", "Lifetime warranty coverage",
            "Personal shopper consultations", "Bilingual customer support",
            "White-glove delivery", "Free design consultations",
            "Hypoallergenic baby clothing", "Non-GMO verified ingredients",
            "FDA-approved medical devices", "Antimicrobial fabric technology",
            "Allergen-free bakery items", "Made-to-order luxury suits",
            "DNA-matched skincare", "Private vineyard blends",
            "Bespoke jewelry design", "Custom fragrance creation",
            "Veteran-owned family business", "Local artist collaborations",
            "Women-empowered workforce", "Neighborhood apprenticeship program",
            "Charity donation per purchase"
    };

    private final String[] businessSpecialties = {
            "Our business helps and supports customers with their needs.",
            "Our business convinces people to buy our products or services.",
            "Our business promotes itself to attract more customers.",
            "Our business manages income, expenses, and financial planning.",
            "Our business keeps proper records of earnings and taxes.",
            "Our business runs daily operations smoothly and efficiently.",
            "Our business hires, trains, and manages staff effectively.",
            "Our business makes key decisions and leads the team forward.",
            "Our business solves technical issues and maintains systems.",
            "Our business builds a strong brand and identity.",
            "Our business connects with customers through social media.",
            "Our business maintains a good online presence.",
            "Our business creates content like text, images, and videos.",
            "Our business follows all legal rules and regulations.",
            "Our business checks product and service quality.",
            "Our business looks for new growth opportunities.",
            "Our business makes long-term plans for success.",
            "Our business researches the market and customer needs.",
            "Our business manages stock and supplies effectively.",
            "Our business purchases the tools and goods needed.",
            "Our business works closely with vendors and partners.",
            "Our business delivers products on time to the right place.",
            "Our business builds long-term customer relationships.",
            "Our business protects and improves its public image.",
            "Our business organizes and manages events.",
            "Our business develops and improves new products.",
            "Our business trains staff to improve their performance.",
            "Our business handles admin tasks and data entry.",
            "Our business creates and maintains websites or apps.",
            "Our business protects important data from threats.",
            "Our business advertises across various platforms.",
            "Our business collects and uses customer feedback.",
            "Our business schedules and manages appointments.",
            "Our business plans the budget for wise spending.",
            "Our business identifies and solves problems before they happen.",
            "आमचा व्यवसाय ग्राहकांच्या गरजांमध्ये मदत आणि पाठिंबा देतो.",
            "आमचा व्यवसाय लोकांना आमची उत्पादने किंवा सेवा घेण्यासाठी प्रेरित करतो.",
            "आमचा व्यवसाय अधिक ग्राहक मिळवण्यासाठी स्वतःची जाहिरात करतो.",
            "आमचा व्यवसाय उत्पन्न, खर्च आणि आर्थिक नियोजन सांभाळतो.",
            "आमचा व्यवसाय कमाई व कराची योग्य नोंद ठेवतो.",
            "आमचा व्यवसाय दररोजची कामे सुरळीत आणि कार्यक्षमतेने चालवतो.",
            "आमचा व्यवसाय कर्मचारी भरती, प्रशिक्षण व व्यवस्थापन करतो.",
            "आमचा व्यवसाय महत्त्वाचे निर्णय घेतो आणि टीमचे नेतृत्व करतो.",
            "आमचा व्यवसाय तांत्रिक अडचणी सोडवतो आणि सिस्टम सांभाळतो.",
            "आमचा व्यवसाय एक मजबूत ब्रँड आणि ओळख तयार करतो.",
            "आमचा व्यवसाय सोशल मीडियाद्वारे ग्राहकांशी जोडतो.",
            "आमचा व्यवसाय ऑनलाइन चांगली उपस्थिती राखतो.",
            "आमचा व्यवसाय मजकूर, चित्रे व व्हिडिओ तयार करतो.",
            "आमचा व्यवसाय सर्व कायदेशीर नियम आणि अटी पाळतो.",
            "आमचा व्यवसाय उत्पादने व सेवेची गुणवत्ता तपासतो.",
            "आमचा व्यवसाय वाढीच्या नव्या संधी शोधतो.",
            "आमचा व्यवसाय यशासाठी दीर्घकालीन योजना तयार करतो.",
            "आमचा व्यवसाय बाजार आणि ग्राहकांच्या गरजांचा अभ्यास करतो.",
            "आमचा व्यवसाय साठा व साहित्य व्यवस्थित ठेवतो.",
            "आमचा व्यवसाय आवश्यक वस्तू व साधने खरेदी करतो.",
            "आमचा व्यवसाय विक्रेत्यांशी आणि भागीदारांशी चांगले संबंध ठेवतो.",
            "आमचा व्यवसाय उत्पादने वेळेवर योग्य ठिकाणी पोहोचवतो.",
            "आमचा व्यवसाय दीर्घकालीन ग्राहक संबंध निर्माण करतो.",
            "आमचा व्यवसाय आपल्या प्रतिमेचे संरक्षण आणि सुधारणा करतो.",
            "आमचा व्यवसाय कार्यक्रम आयोजित करतो व व्यवस्थापन करतो.",
            "आमचा व्यवसाय नवीन उत्पादने तयार करतो आणि सुधारतो.",
            "आमचा व्यवसाय कर्मचाऱ्यांचे प्रशिक्षण देतो.",
            "आमचा व्यवसाय प्रशासकीय कामे आणि माहितीची नोंद घेतो.",
            "आमचा व्यवसाय वेबसाइट्स किंवा अ‍ॅप्स तयार करतो आणि सांभाळतो.",
            "आमचा व्यवसाय महत्त्वाची माहिती सुरक्षित ठेवतो.",
            "आमचा व्यवसाय विविध ठिकाणी जाहिरात करतो.",
            "आमचा व्यवसाय ग्राहक अभिप्राय गोळा करतो आणि वापरतो.",
            "आमचा व्यवसाय अपॉइंटमेंट्स आणि वेळापत्रक सांभाळतो.",
            "आमचा व्यवसाय खर्चासाठी योग्य आर्थिक नियोजन करतो.",
            "आमचा व्यवसाय अडचणी येण्याआधीच त्या ओळखतो आणि सोडवतो."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialties);

        recyclerView = findViewById(R.id.rec1);
        btnGenerate = findViewById(R.id.btnGenerate);
        addSpeci = findViewById(R.id.addspeci);

        specialtiesList = new ArrayList<>();
        specialtiesKeys = new ArrayList<>();

        specialtiesRef = FirebaseDatabase.getInstance().getReference("specialties");

        adapter = new SpecialtiesAdapter(specialtiesList, specialtiesKeys, new SpecialtiesAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(String key, String currentValue) {
                showEditDialog(key, currentValue);
            }

            @Override
            public void onDeleteClick(String key) {
                specialtiesRef.child(key).removeValue()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(SpecialtiesActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(SpecialtiesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadSpecialtiesFromFirebase();

        btnGenerate.setOnClickListener(v -> showSpecialtiesPopup());
        addSpeci.setOnClickListener(v -> showAddDialog());
    }

    private void showSpecialtiesPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.specialties_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ListView listView = dialog.findViewById(R.id.listView);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        // Create a custom adapter that handles animation
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.specialty_item, R.id.textView, businessSpecialties) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Only animate if not already animated
                if (view.getTag() == null) {
                    view.setVisibility(View.INVISIBLE);
                    view.setTag("animated");

                    // Post animation with delay based on position
                    view.postDelayed(() -> {
                        view.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(SpecialtiesActivity.this, R.anim.shutter_down);
                        view.startAnimation(animation);
                    }, position * 200); // 100ms delay between items
                }

                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = businessSpecialties[position];
            if (!specialtiesList.contains(selectedItem)) {
                addSpecialtyToFirebase(selectedItem);
                Toast.makeText(SpecialtiesActivity.this, "Added: " + selectedItem, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SpecialtiesActivity.this, "Already added", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }    private void addSpecialtyToFirebase(String specialty) {
        specialtiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxKey = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        int currentKey = Integer.parseInt(child.getKey());
                        if (currentKey > maxKey) {
                            maxKey = currentKey;
                        }
                    } catch (NumberFormatException e) {
                        // Skip if key is not numeric
                    }
                }
                String newKey = String.valueOf(maxKey + 1);
                specialtiesRef.child(newKey).setValue(specialty)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(SpecialtiesActivity.this, " added succefuly", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(SpecialtiesActivity.this, "Failed to add", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SpecialtiesActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSpecialtiesFromFirebase() {
        specialtiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specialtiesList.clear();
                specialtiesKeys.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String specialty = child.getValue(String.class);
                    String key = child.getKey();
                    specialtiesList.add(specialty);
                    specialtiesKeys.add(key);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SpecialtiesActivity.this, "Failed to load specialties.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAutoCompleteSuggestions(AutoCompleteTextView autoCompleteTextView) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                predefinedSpecialties
        );
        autoCompleteTextView.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_specialty, null);
        builder.setView(dialogView);

        AutoCompleteTextView editText = dialogView.findViewById(R.id.editSpecialty);
        setupAutoCompleteSuggestions(editText);

        Button submitBtn = dialogView.findViewById(R.id.submitSpecialty);
        AlertDialog dialog = builder.create();
        dialog.show();

        submitBtn.setOnClickListener(v -> {
            String specialty = editText.getText().toString().trim();
            if (!specialty.isEmpty()) {
                addSpecialtyToFirebase(specialty);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a specialty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(String key, String currentValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_specialty, null);
        builder.setView(dialogView);

        AutoCompleteTextView editText = dialogView.findViewById(R.id.editSpecialty);
        setupAutoCompleteSuggestions(editText);
        editText.setText(currentValue);

        Button submitBtn = dialogView.findViewById(R.id.submitSpecialty);
        AlertDialog dialog = builder.create();
        dialog.show();

        submitBtn.setOnClickListener(v -> {
            String updatedValue = editText.getText().toString().trim();
            if (!updatedValue.isEmpty()) {
                specialtiesRef.child(key).setValue(updatedValue)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Please enter a valid specialty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}