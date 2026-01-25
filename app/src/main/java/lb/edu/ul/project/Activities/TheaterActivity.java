package lb.edu.ul.project.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.project.Adapters.TheaterAdapter;
import lb.edu.ul.project.Domain.Theater;
import lb.edu.ul.project.R;

public class TheaterActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private RecyclerView theatersRecyclerView;
    private TheaterAdapter theaterAdapter;
    private List<Theater> theaterList;
    private FusedLocationProviderClient fusedLocationClient;
    private MaterialButton openMapButton;
    private String movieTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        movieTitle = getIntent().getStringExtra("movieTitle");
        if (movieTitle == null) movieTitle = "";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Find Nearby Theaters");
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        loadMockTheaters();
        checkLocationPermission();
    }

    private void initViews() {
        theatersRecyclerView = findViewById(R.id.theatersRecyclerView);
        theatersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        theaterList = new ArrayList<>();
        theaterAdapter = new TheaterAdapter(theaterList, this);
        theatersRecyclerView.setAdapter(theaterAdapter);

        openMapButton = findViewById(R.id.openMapButton);
        openMapButton.setOnClickListener(v -> openMapsApp());
    }

    private void loadMockTheaters() {
        theaterList.clear();
        theaterList.add(new Theater("Grand Cinemas ABC Verdun", "ABC Mall, Verdun, Beirut", 1.2, "+961 1 797 111", "10:00 AM - 12:00 AM", 33.8738, 35.4832));
        theaterList.add(new Theater("Cinemacity Beirut Souks", "Beirut Souks, Downtown Beirut", 2.5, "+961 1 988 888", "11:00 AM - 11:30 PM", 33.8965, 35.5053));
        theaterList.add(new Theater("Grand Cinemas - Le Mall Sin el Fil", "Le Mall Sin el Fil, Sin el Fil", 3.8, "+961 1 480 888", "10:00 AM - 12:00 AM", 33.8729, 35.5446));
        theaterList.add(new Theater("Cinemacity Dbayeh", "Mall of Lebanon, Dbayeh", 4.2, "+961 4 719 500", "10:30 AM - 11:30 PM", 33.9467, 35.6015));
        theaterList.add(new Theater("Empire Cinemas The Spot", "The Spot Mall, Saida", 5.1, "+961 7 751 000", "11:00 AM - 11:00 PM", 33.5630, 35.3708));
        theaterList.add(new Theater("Metropolis Cinema Sofil", "Metropolis Empire, Dora", 6.0, "+961 1 240 240", "10:00 AM - 12:00 AM", 33.9167, 35.5667));
        theaterAdapter.notifyDataSetChanged();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                    .setTitle("Location Permission Required")
                    .setMessage("This app needs location access to find nearby movie theaters. Please grant location permission.")
                    .setPositiveButton("Grant", (dialog, which) -> requestLocationPermission())
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        Toast.makeText(this, "Location permission denied. Showing mock theaters.", Toast.LENGTH_LONG).show();
                    })
                    .show();
            } else {
                requestLocationPermission();
            }
        } else {
            getCurrentLocation();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted!", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Showing mock theaters.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Toast.makeText(this, "Location found: " + location.getLatitude() + ", " + location.getLongitude(), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openMapsApp() {
        String query = movieTitle.isEmpty() ? "movie theaters near me" : "movie theaters showing " + movieTitle;
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webUri = Uri.parse("https://www.google.com/maps/search/" + Uri.encode(query));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }
}
