package com.example.westyorkshirecrimeapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.io.*;


public class DashboardActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button mapButton, importButton;
    private RecyclerView recyclerView;
    private CrimeAdapter adapter;
    private List<Crime> crimeList = new ArrayList<>();
    private List<Crime> currentFilteredList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String currentUserId; // Declared at class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // VISUALS: Set the top status bar color
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.holo_blue_dark));

        // 1. Initialize UI Components
        searchBar = findViewById(R.id.searchBar);
        mapButton = findViewById(R.id.mapButton);
        importButton = findViewById(R.id.importButton);

        // Hide it immediately so non-admins never see it
        importButton.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.crimeRecyclerView);

        // 2. Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // FIX: Assign to the class-level variable
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // 3. Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrimeAdapter(crimeList);
        recyclerView.setAdapter(adapter);

        searchBar.requestFocus();

        // 4. Check for Admin Role (Matches your 'role: admin' database structure)
        checkUserRole();

        // 5. Fetch Data from Firebase
        fetchCrimes();

        // 6. Search Bar Listener (Filters the list as you type)
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            //reactive filtering

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString()); // Update the list as the user types
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 7. Map Button Listener (Passes data to the Map screen), Pack the "Locker" (MapDataHolder) and open the map
        mapButton.setOnClickListener(v -> {
            if (currentFilteredList.isEmpty() && searchBar.getText().toString().isEmpty()) {
                MapDataHolder.listOfCrimesToShow = crimeList;
            } else {
                MapDataHolder.listOfCrimesToShow = currentFilteredList;
            }

            Intent intent = new Intent(DashboardActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        // 8. Import Button Listener (Admin only)
        importButton.setOnClickListener(v -> importCSVData());

        // 9. Click Listener (So something happens when you click a record)
        adapter.setOnItemClickListener(crime -> {
            Intent intent = new Intent(DashboardActivity.this, CrimeDetailActivity.class);
            intent.putExtra("crime_data", crime);
            startActivity(intent);
        });

        // 10. UPDATED: Long Click for Deletion (NOW ADMIN ONLY)
        adapter.setOnItemLongClickListener(crime -> {
            if (currentUserId == null) return;

            // Check role again before allowing delete popup
            mDatabase.child("Users").child(currentUserId).child("role").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String role = String.valueOf(task.getResult().getValue());
                            if ("admin".equals(role)) {
                                handleDeleteRequest(crime); // Only admins see the dialog
                            } else {
                                Toast.makeText(this, "Only admins can delete records", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

    }

    // This method tells the app what to do when "Logout" is clicked via ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            handleLogoutClick(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This connects your existing XML file to the Activity
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    private void filterList(String text) {
        currentFilteredList.clear();
        for (Crime crime : crimeList) {
            if (crime.crimeType.toLowerCase().contains(text.toLowerCase()) ||
                    crime.location.toLowerCase().contains(text.toLowerCase())) {
                currentFilteredList.add(crime);
            }
        }
        adapter.setFilteredList(currentFilteredList);
    }

    private void checkUserRole() {
        if (currentUserId == null) return;

        mDatabase.child("Users").child(currentUserId).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String role = snapshot.getValue(String.class);
                            if ("admin".equals(role)) {
                                importButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    private void importCSVData() {
        // Only wrap the heavy work in a thread;
        new Thread(() -> {
            try {
                InputStream is = getAssets().open("crimeyorkshire.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length >= 11) {
                        Crime crime = new Crime(
                                tokens[0], tokens[1], tokens[2], tokens[3],
                                Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]),
                                tokens[6], tokens[7], tokens[8], tokens[9], tokens[10],
                                tokens.length > 11 ? tokens[11] : ""
                        );
                        mDatabase.child("Crimes").child(tokens[0]).setValue(crime);
                    }
                }
                // To show a Toast from a background thread, you must use runOnUiThread
                runOnUiThread(() -> Toast.makeText(this, "Data Imported!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void fetchCrimes() {
        mDatabase.child("Crimes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                crimeList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Crime crime = data.getValue(Crime.class);
                    crimeList.add(crime);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void handleDeleteRequest(Crime crime) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this crime record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mDatabase.child("Crimes").child(crime.crimeID).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Error deleting", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void handleLogoutClick(View v) {
        FirebaseAuth.getInstance().signOut();
        if (MapDataHolder.listOfCrimesToShow != null) {
            MapDataHolder.listOfCrimesToShow.clear();
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}