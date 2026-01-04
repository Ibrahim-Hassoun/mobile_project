package lb.edu.ul.project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.project.Adapters.SearchMovieAdapter;
import lb.edu.ul.project.Domain.Datum;
import lb.edu.ul.project.Domain.ListFilm;
import lb.edu.ul.project.R;

public class SearchMovieActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private SearchMovieAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private RequestQueue requestQueue;
    private List<Datum> allMovies;
    private List<Datum> filteredMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add to Watchlist");
        }

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.recyclerViewSearch);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        allMovies = new ArrayList<>();
        filteredMovies = new ArrayList<>();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchMovieAdapter(filteredMovies, this::onMovieSelected);
        recyclerView.setAdapter(adapter);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Load movies
        loadMovies();

        // Set up search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovies() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Load multiple pages to get more movies
        loadMoviesFromPage(1);
        loadMoviesFromPage(2);
        loadMoviesFromPage(3);
    }

    private void loadMoviesFromPage(int page) {
        String url = "https://moviesapi.ir/api/v1/movies?page=" + page;
        
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    ListFilm listFilm = gson.fromJson(response, ListFilm.class);
                    
                    if (listFilm != null && listFilm.getData() != null) {
                        allMovies.addAll(listFilm.getData());
                        filteredMovies.clear();
                        filteredMovies.addAll(allMovies);
                        adapter.notifyDataSetChanged();
                    }
                    
                    progressBar.setVisibility(View.GONE);
                    updateEmptyView();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("SearchMovieActivity", "Error: " + error.toString());
                    updateEmptyView();
                }
            });
        
        requestQueue.add(stringRequest);
    }

    private void filterMovies(String query) {
        filteredMovies.clear();
        
        if (query.isEmpty()) {
            filteredMovies.addAll(allMovies);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Datum movie : allMovies) {
                if (movie.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredMovies.add(movie);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private void onMovieSelected(Datum movie) {
        // Return the selected movie to WatchlistActivity
        Intent resultIntent = new Intent();
        Gson gson = new Gson();
        String movieJson = gson.toJson(movie);
        resultIntent.putExtra("movie", movieJson);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateEmptyView() {
        if (filteredMovies.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
