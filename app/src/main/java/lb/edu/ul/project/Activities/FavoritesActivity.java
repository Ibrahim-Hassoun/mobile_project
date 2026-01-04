package lb.edu.ul.project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lb.edu.ul.project.Adapters.FilmListAdapter;
import lb.edu.ul.project.Database.FavoritesDatabaseHelper;
import lb.edu.ul.project.Domain.Datum;
import lb.edu.ul.project.R;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FilmListAdapter adapter;
    private FavoritesDatabaseHelper dbHelper;
    private ImageView backBtn;
    private TextView noFavoritesMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new FavoritesDatabaseHelper(this);
        backBtn = findViewById(R.id.backImg);
        noFavoritesMessage = findViewById(R.id.noFavoritesMessage); // Add this in XML

        loadFavorites();

        // Go back to previous activity
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadFavorites() {
        ArrayList<Datum> favoriteMovies = dbHelper.getFavorites();
        Log.d("FavoritesActivity", "Retrieved " + favoriteMovies.size() + " favorite movies");

        if (favoriteMovies.isEmpty()) {
            noFavoritesMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "No favorite movies found!", Toast.LENGTH_SHORT).show();
        } else {
            noFavoritesMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new FilmListAdapter(this, favoriteMovies);
            recyclerView.setAdapter(adapter);
        }
    }
}
