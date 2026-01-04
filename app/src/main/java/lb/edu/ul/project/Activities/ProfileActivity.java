package lb.edu.ul.project.Activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.project.Domain.UserProfile;
import lb.edu.ul.project.R;

public class ProfileActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String PROFILE_KEY = "user_profile";

    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView bioTextView;
    private ChipGroup genresChipGroup;
    private Button editProfileButton;
    private TextView statsMoviesText;
    private TextView statsReviewsText;
    private TextView statsWatchlistText;

    private UserProfile userProfile;
    private SharedPreferences prefs;
    private boolean isEditMode = false;

    private final String[] availableGenres = {
        "Action", "Comedy", "Drama", "Horror", "Sci-Fi", 
        "Thriller", "Romance", "Adventure", "Animation", "Documentary"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        initViews();
        loadProfile();
        updateUI();
        updateStats();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        bioTextView = findViewById(R.id.bioTextView);
        genresChipGroup = findViewById(R.id.genresChipGroup);
        editProfileButton = findViewById(R.id.editProfileButton);
        statsMoviesText = findViewById(R.id.statsMoviesText);
        statsReviewsText = findViewById(R.id.statsReviewsText);
        statsWatchlistText = findViewById(R.id.statsWatchlistText);

        editProfileButton.setOnClickListener(v -> showEditDialog());
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProfile() {
        String json = prefs.getString(PROFILE_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            userProfile = gson.fromJson(json, UserProfile.class);
        } else {
            // Create default profile
            userProfile = new UserProfile("Guest User", "guest@movieapp.com", "Movie enthusiast");
            userProfile.getFavoriteGenres().add("Action");
            userProfile.getFavoriteGenres().add("Sci-Fi");
        }
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userProfile);
        editor.putString(PROFILE_KEY, json);
        editor.apply();
    }

    private void updateUI() {
        usernameTextView.setText(userProfile.getUsername());
        emailTextView.setText(userProfile.getEmail());
        bioTextView.setText(userProfile.getBio());

        // Update favorite genres chips
        genresChipGroup.removeAllViews();
        for (String genre : userProfile.getFavoriteGenres()) {
            Chip chip = new Chip(this);
            chip.setText(genre);
            chip.setChipBackgroundColorResource(R.color.yellow);
            chip.setTextColor(getResources().getColor(R.color.main_color));
            genresChipGroup.addView(chip);
        }

        if (userProfile.getFavoriteGenres().isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No favorite genres selected");
            emptyText.setTextColor(getResources().getColor(R.color.white));
            genresChipGroup.addView(emptyText);
        }
    }

    private void updateStats() {
        // Get stats from SharedPreferences
        SharedPreferences reviewsPrefs = getSharedPreferences("ReviewsPrefs", MODE_PRIVATE);
        SharedPreferences watchlistPrefs = getSharedPreferences("WatchlistPrefs", MODE_PRIVATE);
        SharedPreferences favoritesPrefs = getSharedPreferences("FavoritesPrefs", MODE_PRIVATE);

        // Count reviews
        String reviewsJson = reviewsPrefs.getString("reviews", null);
        int reviewCount = 0;
        if (reviewsJson != null && !reviewsJson.equals("[]")) {
            reviewCount = reviewsJson.split("\\{").length - 1;
        }

        // Count watchlist
        String watchlistJson = watchlistPrefs.getString("watchlist", null);
        int watchlistCount = 0;
        if (watchlistJson != null && !watchlistJson.equals("[]")) {
            watchlistCount = watchlistJson.split("\\{").length - 1;
        }

        // Update UI
        statsReviewsText.setText(String.valueOf(reviewCount));
        statsWatchlistText.setText(String.valueOf(watchlistCount));
    }

    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText usernameEdit = dialogView.findViewById(R.id.usernameEditText);
        EditText emailEdit = dialogView.findViewById(R.id.emailEditText);
        EditText bioEdit = dialogView.findViewById(R.id.bioEditText);
        ChipGroup genresChipGroupDialog = dialogView.findViewById(R.id.genresChipGroupDialog);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Pre-fill current values
        usernameEdit.setText(userProfile.getUsername());
        emailEdit.setText(userProfile.getEmail());
        bioEdit.setText(userProfile.getBio());

        // Add genre chips
        List<String> selectedGenres = new ArrayList<>(userProfile.getFavoriteGenres());
        for (String genre : availableGenres) {
            Chip chip = new Chip(this);
            chip.setText(genre);
            chip.setCheckable(true);
            chip.setChecked(selectedGenres.contains(genre));
            chip.setChipBackgroundColorResource(R.color.main_color);
            chip.setTextColor(getResources().getColor(R.color.white));
            chip.setCheckedIconVisible(true);
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedGenres.contains(genre)) {
                        selectedGenres.add(genre);
                    }
                } else {
                    selectedGenres.remove(genre);
                }
            });
            
            genresChipGroupDialog.addView(chip);
        }

        saveButton.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();
            String bio = bioEdit.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            userProfile.setUsername(username);
            userProfile.setEmail(email);
            userProfile.setBio(bio);
            userProfile.setFavoriteGenres(selectedGenres);

            saveProfile();
            updateUI();
            dialog.dismiss();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
