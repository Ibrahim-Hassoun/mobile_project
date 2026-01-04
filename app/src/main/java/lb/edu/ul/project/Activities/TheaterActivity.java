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
        theaterList.add(new Theater("Grand Cinema", "123 Main Street, Downtown", 1.2, "+1 555-0101", "10:00 AM - 11:00 PM"));
        theaterList.add(new Theater("Starlight Theater", "456 Oak Avenue, City Center", 2.5, "+1 555-0102", "9:00 AM - 12:00 AM"));
        theaterList.add(new Theater("Regal Cinemas", "789 Elm Street, North Side", 3.8, "+1 555-0103", "11:00 AM - 10:00 PM"));
        theaterList.add(new Theater("AMC Multiplex", "321 Pine Road, West End", 4.2, "+1 555-0104", "10:30 AM - 11:30 PM"));
        theaterList.add(new Theater("Cinema Paradise", "654 Maple Drive, East District", 5.1, "+1 555-0105", "9:30 AM - 10:30 PM"));
        theaterList.add(new Theater("Movie Palace", "987 Cedar Lane, South Quarter", 6.0, "+1 555-0106", "10:00 AM - 11:00 PM"));
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
