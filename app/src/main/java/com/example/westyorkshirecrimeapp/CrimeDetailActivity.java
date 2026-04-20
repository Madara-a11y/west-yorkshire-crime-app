package com.example.westyorkshirecrimeapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CrimeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_detail);
        // Link the button from your XML
        Button btnBack = findViewById(R.id.btnBackToDashboard);

// Set the action to return to the Dashboard
        btnBack.setOnClickListener(v -> {
            // finish() closes the current page, revealing the Dashboard underneath
            finish();
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crime Details");
        }

        // Get the crime object passed from the Dashboard
        Crime crime = (Crime) getIntent().getSerializableExtra("crime_data");

        if (crime != null) {
            TextView details = findViewById(R.id.txtCrimeDetails);
            details.setText(
                    "Type: " + crime.crimeType + "\n" +
                            "Location: " + crime.location + "\n" +
                            "Status: " + crime.lastOutcome + "\n" +
                            "Date: " + crime.month
            );
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}