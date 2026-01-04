package lb.edu.ul.project.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    private ImageView pic2, backImg, favImg;
    private RecyclerView.Adapter adapterActorList, adapterCategory, reviewAdapter;
    private RecyclerView recyclerViewActors, recyclerViewCategory, recyclerViewReviews;
    private NestedScrollView scrollView;
    private FavoritesDatabaseHelper dbHelper;
    private FilmItem currentFilm;
    private Button writeReviewButton, findTheaterButton;
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
        writeReviewButton.setOnClickListener(v -> showWriteReviewDialog());
        findTheaterButton.setOnClickListener(v -> openTheaterActivity());
    }

    private void sendRequest() {
        mRequestQueue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        StringRequest mStringRequest = new StringRequest(Request.Method.GET,
                "https://moviesapi.ir/api/v1/movies/" + idFilm,
                response -> {
                    Gson gson = new Gson();
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                    currentFilm = gson.fromJson(response, FilmItem.class);

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
                },
                error -> progressBar.setVisibility(View.GONE));

        mRequestQueue.add(mStringRequest);
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
        recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewActors = findViewById(R.id.imagesRecycler);
        recyclerViewReviews = findViewById(R.id.reviewsRecyclerView);
        reviewsCountText = findViewById(R.id.reviewsCountText);
        noReviewsText = findViewById(R.id.noReviewsText);
        writeReviewButton = findViewById(R.id.writeReviewButton);
        findTheaterButton = findViewById(R.id.findTheaterButton);

        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

        backImg.setOnClickListener(v -> finish());
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
}
