package lb.edu.ul.project.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.project.Adapters.ActorsListAdapter;
import lb.edu.ul.project.Adapters.CategoryEachFilmListAdapter;
import lb.edu.ul.project.Adapters.ReviewAdapter;
import lb.edu.ul.project.Database.FavoritesDatabaseHelper;
import lb.edu.ul.project.Domain.FilmItem;
import lb.edu.ul.project.Domain.Person;
import lb.edu.ul.project.Domain.Review;
import lb.edu.ul.project.R;

public class DetailActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "ReviewsPrefs";
    private static final String REVIEWS_KEY = "reviews";

    private RequestQueue mRequestQueue;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRateTxt, movieTimeTxt, movieSummaryInfo, movieActorsInfo;
    private TextView reviewsCountText, noReviewsText;
    private int idFilm;
    private ImageView pic2, backImg, favImg, shareImg;
    private RecyclerView.Adapter adapterActorList, adapterCategory, reviewAdapter;
    private RecyclerView recyclerViewActors, recyclerViewCategory, recyclerViewReviews;
    private NestedScrollView scrollView;
    private FavoritesDatabaseHelper dbHelper;
    private FilmItem currentFilm;
    private Button writeReviewButton, findTheaterButton, setReminderButton;
    private List<Review> movieReviews;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        idFilm = getIntent().getIntExtra("id", 0);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dbHelper = new FavoritesDatabaseHelper(this);
        
        initView();
        sendRequest();
        loadReviews();

        updateFavoriteIcon(dbHelper.isFavorite(idFilm));

        favImg.setOnClickListener(v -> toggleFavorite());
        shareImg.setOnClickListener(v -> shareMovie());
        writeReviewButton.setOnClickListener(v -> showWriteReviewDialog());
        findTheaterButton.setOnClickListener(v -> openTheaterActivity());
        setReminderButton.setOnClickListener(v -> showSetReminderDialog());
    }

    private void sendRequest() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        try {
            String json = loadJSONFromAsset("movie_details.json");
            Gson gson = new Gson();
            
            Type type = new TypeToken<java.util.Map<String, FilmItem>>(){}.getType();
            java.util.Map<String, FilmItem> moviesMap = gson.fromJson(json, type);
            
            currentFilm = moviesMap.get(String.valueOf(idFilm));
            
            if (currentFilm != null) {
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                Glide.with(DetailActivity.this)
                        .load(currentFilm.getPoster())
                        .into(pic2);

                titleTxt.setText(currentFilm.getTitle());
                movieRateTxt.setText(currentFilm.getImdbRating());
                movieTimeTxt.setText(currentFilm.getRuntime());
                movieSummaryInfo.setText(currentFilm.getPlot());
                movieActorsInfo.setText(currentFilm.getActors());

                if (currentFilm.getImages() != null) {
                    adapterActorList = new ActorsListAdapter(currentFilm.getImages());
                    recyclerViewActors.setAdapter(adapterActorList);
                }
                if (currentFilm.getGenres() != null) {
                    adapterCategory = new CategoryEachFilmListAdapter(currentFilm.getGenres());
                    recyclerViewCategory.setAdapter(adapterCategory);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Log.e("DetailActivity", "Error loading movie: " + e.getMessage());
            Toast.makeText(this, "Error loading movie", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReviews() {
        String json = prefs.getString(REVIEWS_KEY, null);
        List<Review> allReviews = new ArrayList<>();

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Review>>() {}.getType();
            allReviews = gson.fromJson(json, type);
        }

        // Filter reviews for current movie
        movieReviews = new ArrayList<>();
        for (Review review : allReviews) {
            if (review.getMovieId() == idFilm) {
                movieReviews.add(review);
            }
        }

        updateReviewsUI();
    }

    private void updateReviewsUI() {
        if (movieReviews.isEmpty()) {
            recyclerViewReviews.setVisibility(View.GONE);
            noReviewsText.setVisibility(View.VISIBLE);
            reviewsCountText.setText("Reviews (0)");
        } else {
            recyclerViewReviews.setVisibility(View.VISIBLE);
            noReviewsText.setVisibility(View.GONE);
            reviewsCountText.setText("Reviews (" + movieReviews.size() + ")");
            reviewAdapter = new ReviewAdapter(movieReviews);
            recyclerViewReviews.setAdapter(reviewAdapter);
        }
    }

    private void showWriteReviewDialog() {
        if (currentFilm == null) {
            Toast.makeText(this, "Movie details not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_write_review, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        TextView ratingValueText = dialogView.findViewById(R.id.ratingValueText);
        EditText reviewEditText = dialogView.findViewById(R.id.reviewEditText);
        Button submitButton = dialogView.findViewById(R.id.submitButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> 
            ratingValueText.setText(String.format("%.1f", rating)));

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String reviewText = reviewEditText.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show();
                return;
            }

            saveReview(rating, reviewText);
            dialog.dismiss();
            Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveReview(float rating, String reviewText) {
        Review newReview = new Review(idFilm, currentFilm.getTitle(), rating, reviewText, "Guest User");

        // Load all reviews
        String json = prefs.getString(REVIEWS_KEY, null);
        List<Review> allReviews = new ArrayList<>();

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Review>>() {}.getType();
            allReviews = gson.fromJson(json, type);
        }

        // Add new review
        allReviews.add(0, newReview);

        // Save back
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(allReviews);
        editor.putString(REVIEWS_KEY, updatedJson);
        editor.apply();

        // Reload reviews
        loadReviews();
    }

    private void toggleFavorite() {
        if (currentFilm == null) {
            Toast.makeText(this, "Movie details not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isFavorite(idFilm)) {
            boolean removed = dbHelper.removeFavorite(idFilm);
            if (removed) {
                updateFavoriteIcon(false);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean added = dbHelper.addFavorite(idFilm, currentFilm.getTitle(), currentFilm.getPoster());
            if (added) {
                updateFavoriteIcon(true);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            favImg.setImageResource(R.drawable.fav_filled);
        } else {
            favImg.setImageResource(R.drawable.fav);
        }
    }

    private void initView() {
        titleTxt = findViewById(R.id.movieNameTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollView2);
        pic2 = findViewById(R.id.picDetail);
        movieRateTxt = findViewById(R.id.movieStar);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieSummaryInfo = findViewById(R.id.movieSummary);
        movieActorsInfo = findViewById(R.id.movieActorInfo);
        backImg = findViewById(R.id.backImg);
        favImg = findViewById(R.id.imageView8);
        shareImg = findViewById(R.id.shareImg);
        recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewActors = findViewById(R.id.imagesRecycler);
        recyclerViewReviews = findViewById(R.id.reviewsRecyclerView);
        reviewsCountText = findViewById(R.id.reviewsCountText);
        noReviewsText = findViewById(R.id.noReviewsText);
        writeReviewButton = findViewById(R.id.writeReviewButton);
        findTheaterButton = findViewById(R.id.findTheaterButton);
        setReminderButton = findViewById(R.id.setReminderButton);

        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

        backImg.setOnClickListener(v -> finish());
        
        movieActorsInfo.setOnClickListener(v -> {
            if (currentFilm != null && currentFilm.getActors() != null) {
                openPersonProfile(currentFilm.getActors(), "Actor");
            }
        });
    }

    private void openTheaterActivity() {
        if (currentFilm == null) {
            Toast.makeText(this, "Movie details not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, TheaterActivity.class);
        intent.putExtra("movieTitle", currentFilm.getTitle());
        startActivity(intent);
    }

    private void showSetReminderDialog() {
        if (currentFilm == null) {
            Toast.makeText(this, "Movie details not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle("Set Release Reminder")
            .setMessage("Would you like to add \"" + currentFilm.getTitle() + "\" to your calendar?\n\nRelease Date: " + currentFilm.getReleased())
            .setPositiveButton("Add to Calendar", (dialog, which) -> setCalendarReminder())
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_menu_my_calendar)
            .show();
    }

    private void setCalendarReminder() {
        try {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            
            intent.putExtra(CalendarContract.Events.TITLE, currentFilm.getTitle() + " - Movie Release");
            intent.putExtra(CalendarContract.Events.DESCRIPTION, 
                "Don't miss the release of " + currentFilm.getTitle() + "!\n\n" +
                "IMDb Rating: " + currentFilm.getImdbRating() + "\n" +
                "Director: " + currentFilm.getDirector());
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Theaters");
            intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
            
            // Parse release date if possible, otherwise use a default time
            String releaseDate = currentFilm.getReleased();
            if (releaseDate != null && !releaseDate.isEmpty()) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH);
                    java.util.Date date = sdf.parse(releaseDate);
                    if (date != null) {
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.getTime());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.getTime() + 3600000); // +1 hour
                        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                    }
                } catch (Exception e) {
                    // If parsing fails, let user set date manually
                }
            }
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                Toast.makeText(this, "Opening calendar...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No calendar app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening calendar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openPersonProfile(String actorsString, String role) {
        if (actorsString == null || actorsString.isEmpty()) {
            Toast.makeText(this, "No " + role.toLowerCase() + " information available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Split actors by comma and show dialog to select one
        String[] actors = actorsString.split(",");
        
        if (actors.length == 1) {
            // If only one actor, open directly
            openPersonProfileActivity(actors[0].trim(), role);
        } else {
            // Show dialog to select actor
            new AlertDialog.Builder(this)
                .setTitle("Select " + role)
                .setItems(actors, (dialog, which) -> {
                    openPersonProfileActivity(actors[which].trim(), role);
                })
                .show();
        }
    }

    private void openPersonProfileActivity(String personName, String role) {
        Person person = new Person(personName, role);
        
        // Create mock data for demonstration
        person.setBiography("A talented " + role.toLowerCase() + " known for outstanding performances in various films. "
            + "With a career spanning multiple decades, " + personName + " has become one of the most recognizable names in cinema.");
        person.setBirthDate("January 15, 1975");
        
        java.util.List<String> knownFor = new java.util.ArrayList<>();
        if (currentFilm != null) {
            knownFor.add(currentFilm.getTitle());
        }
        knownFor.add("The Great Adventure");
        knownFor.add("Mystery Night");
        person.setKnownFor(knownFor);
        
        // Create mock filmography
        java.util.List<Person.FilmCredit> filmography = new java.util.ArrayList<>();
        if (currentFilm != null) {
            Person.FilmCredit credit = new Person.FilmCredit(
                currentFilm.getId(),
                currentFilm.getTitle(),
                currentFilm.getYear(),
                currentFilm.getPoster()
            );
            filmography.add(credit);
        }
        person.setFilmography(filmography);
        
        Intent intent = new Intent(this, PersonProfileActivity.class);
        intent.putExtra("person", person);
        startActivity(intent);
    }

    private void shareMovie() {
        if (currentFilm == null) {
            Toast.makeText(this, "Movie details not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create share text with movie details
        StringBuilder shareText = new StringBuilder();
        shareText.append("🎬 Check out this movie!\n\n");
        shareText.append("📽️ ").append(currentFilm.getTitle());
        
        if (currentFilm.getYear() != null && !currentFilm.getYear().isEmpty()) {
            shareText.append(" (").append(currentFilm.getYear()).append(")");
        }
        shareText.append("\n\n");
        
        if (currentFilm.getImdbRating() != null && !currentFilm.getImdbRating().isEmpty()) {
            shareText.append("⭐ IMDb Rating: ").append(currentFilm.getImdbRating()).append("\n");
        }
        
        if (currentFilm.getDirector() != null && !currentFilm.getDirector().isEmpty()) {
            shareText.append("🎥 Director: ").append(currentFilm.getDirector()).append("\n");
        }
        
        if (currentFilm.getActors() != null && !currentFilm.getActors().isEmpty()) {
            shareText.append("🎭 Starring: ").append(currentFilm.getActors()).append("\n");
        }
        
        if (currentFilm.getPlot() != null && !currentFilm.getPlot().isEmpty()) {
            shareText.append("\n📖 ").append(currentFilm.getPlot());
        }

        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentFilm.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        // Show app chooser
        Intent chooser = Intent.createChooser(shareIntent, "Share " + currentFilm.getTitle() + " via");
        
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(this, "No apps available to share", Toast.LENGTH_SHORT).show();
        }
    }

    private String loadJSONFromAsset(String filename) {
        String json;
        try {
            java.io.InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
