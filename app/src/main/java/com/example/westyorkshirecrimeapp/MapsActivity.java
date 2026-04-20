package com.example.westyorkshirecrimeapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.westyorkshirecrimeapp.databinding.ActivityMapsBinding;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // 1. ADD BACK ARROW: Makes the '<-' appear in the top toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crime Map");
        }

        // 2. FIND THE MAP: Look in XML for the map area
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // 3. START LOADING: Tell Google to prepare the map in the background
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // 4. GO BACK: Closes this screen when the top-left arrow is clicked
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // 5. THE MAIN EVENT: This runs ONLY when the map is fully loaded and ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; // Save the "Live Map" into our variable

        // 6. OPEN THE LOCKER: Get the list of crimes we saved in the Dashboard
        List<Crime> crimesToPlot = MapDataHolder.listOfCrimesToShow;

        if (crimesToPlot != null && !crimesToPlot.isEmpty()) {

            // 7. THE DRAWING LOOP: Go through every crime and drop a pin
            for (Crime crime : crimesToPlot) {
                if (crime.latitude != 0 && crime.longitude != 0) {

                    // Create a Map Coordinate (LatLng)
                    LatLng position = new LatLng(crime.latitude, crime.longitude);

                    // Drop the Pin with Title (Bold) and Snippet (Description)
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(crime.crimeType)
                            .snippet(crime.location));
                }
            }

            // 8. CAMERA MOVE: "Fly" the camera to the first crime and Zoom in (Level 10)
            Crime first = crimesToPlot.get(0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(first.latitude, first.longitude), 10));
        } else {
            // FEEDBACK: Tell the user if there were no results to map
            Toast.makeText(this, "No crimes to display", Toast.LENGTH_SHORT).show();
        }
    }
}