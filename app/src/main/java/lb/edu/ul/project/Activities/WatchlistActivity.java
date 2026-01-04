package lb.edu.ul.project.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.project.Adapters.WatchlistAdapter;
import lb.edu.ul.project.Domain.Datum;
import lb.edu.ul.project.R;

public class WatchlistActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_MOVIE = 1;
    private static final String PREFS_NAME = "WatchlistPrefs";
    private static final String WATCHLIST_KEY = "watchlist";

    private RecyclerView recyclerView;
    private WatchlistAdapter adapter;
    private List<Datum> watchlist;
    private TextView emptyView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Watchlist");
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewWatchlist);
        emptyView = findViewById(R.id.emptyView);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load watchlist from SharedPreferences
        loadWatchlist();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WatchlistAdapter(watchlist, this::removeFromWatchlist, this::onMovieClick);
        recyclerView.setAdapter(adapter);

        updateEmptyView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_watchlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_search) {
            // Open search activity to add movies
            Intent intent = new Intent(WatchlistActivity.this, SearchMovieActivity.class);
            startActivityForResult(intent, REQUEST_ADD_MOVIE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_MOVIE && resultCode == RESULT_OK && data != null) {
            // Get the movie from the search activity
            String movieJson = data.getStringExtra("movie");
            if (movieJson != null) {
                Gson gson = new Gson();
                Datum movie = gson.fromJson(movieJson, Datum.class);
                addToWatchlist(movie);
            }
        }
    }

    private void loadWatchlist() {
        String json = prefs.getString(WATCHLIST_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Datum>>() {}.getType();
            watchlist = gson.fromJson(json, type);
        } else {
            watchlist = new ArrayList<>();
        }
    }

    private void saveWatchlist() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(watchlist);
        editor.putString(WATCHLIST_KEY, json);
        editor.apply();
    }

    private void addToWatchlist(Datum movie) {
        // Check if movie already exists in watchlist
        for (Datum item : watchlist) {
            if (item.getId().equals(movie.getId())) {
                return; // Already in watchlist
            }
        }

        watchlist.add(0, movie);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
        saveWatchlist();
        updateEmptyView();
    }

    private void removeFromWatchlist(int position) {
        watchlist.remove(position);
        adapter.notifyItemRemoved(position);
        saveWatchlist();
        updateEmptyView();
    }

    private void onMovieClick(Datum movie) {
        // Open detail activity
        Intent intent = new Intent(WatchlistActivity.this, DetailActivity.class);
        intent.putExtra("id", movie.getId());
        startActivity(intent);
    }

    private void updateEmptyView() {
        if (watchlist.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
