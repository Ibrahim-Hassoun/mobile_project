/*package lb.edu.ul.project.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import lb.edu.ul.project.Adapters.ActorsListAdapter;
import lb.edu.ul.project.Adapters.CategoryEachFilmListAdapter;
import lb.edu.ul.project.Domain.FilmItem;
import lb.edu.ul.project.R;

public class DetailActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRateTxt,movieTimeTxt,movieSummaryInfo,movieActorsInfo;
    private int idFilm;
    private ImageView pic2,backImg;
    private RecyclerView.Adapter adapterActorList,adapterCategory;
    private RecyclerView recyclerViewActors, recyclerViewCategory;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        idFilm=getIntent().getIntExtra("id",0);
        initView();
        sendRequest();
    }

    private void sendRequest(){
        mRequestQueue= Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        mStringRequest=new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + idFilm, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson=new Gson();
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                FilmItem item = gson.fromJson(response,FilmItem.class);

                Glide.with(DetailActivity.this)
                        .load(item.getPoster())
                        .into(pic2);

                titleTxt.setText(item.getTitle());
                movieRateTxt.setText(item.getImdbRating());
                movieTimeTxt.setText(item.getRuntime());
                movieSummaryInfo.setText(item.getPlot());
                movieActorsInfo.setText(item.getActors());
                if(item.getImages() != null){
                    adapterActorList=new ActorsListAdapter(item.getImages());
                    recyclerViewActors.setAdapter(adapterActorList);
                }
                if(item.getGenres() != null){
                    adapterCategory=new CategoryEachFilmListAdapter(item.getGenres());
                    recyclerViewCategory.setAdapter(adapterCategory);

                }
            }
        }, error -> progressBar.setVisibility(View.GONE));
        mRequestQueue.add(mStringRequest);
    }

    private void initView(){
        titleTxt=findViewById(R.id.movieNameTxt);
        progressBar=findViewById(R.id.progressBarDetail);
        scrollView=findViewById(R.id.scrollView2);
        pic2=findViewById(R.id.picDetail);
        movieRateTxt=findViewById(R.id.movieStar);
        movieTimeTxt=findViewById(R.id.movieTime);
        movieSummaryInfo=findViewById(R.id.movieSummary);
        movieActorsInfo=findViewById(R.id.movieActorInfo);
        backImg=findViewById(R.id.backImg);
        recyclerViewCategory=findViewById(R.id.genreView);
        recyclerViewActors=findViewById(R.id.imagesRecycler);
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        backImg.setOnClickListener(v -> finish());
    }
}
 */
package lb.edu.ul.project.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import lb.edu.ul.project.Adapters.ActorsListAdapter;
import lb.edu.ul.project.Adapters.CategoryEachFilmListAdapter;
import lb.edu.ul.project.Database.FavoritesDatabaseHelper;
import lb.edu.ul.project.Domain.FilmItem;
import lb.edu.ul.project.R;

public class DetailActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRateTxt,movieTimeTxt,movieSummaryInfo,movieActorsInfo;
    private int idFilm;
    private ImageView pic2,backImg;
    private RecyclerView.Adapter adapterActorList,adapterCategory;
    private RecyclerView recyclerViewActors, recyclerViewCategory;
    private NestedScrollView scrollView;
    private ImageView favImg;
    private FavoritesDatabaseHelper dbHelper;
    private FilmItem currentFilm; // Store movie details globally


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        idFilm=getIntent().getIntExtra("id",0);
        initView();
        sendRequest();

        // Check if movie is a favorite
        updateFavoriteIcon(dbHelper.isFavorite(idFilm));

        favImg.setOnClickListener(v -> {
            if (currentFilm == null) {
                Toast.makeText(this, "Movie details not loaded yet!", Toast.LENGTH_SHORT).show();
                return;
            }

            String movieTitle = currentFilm.getTitle();
            String moviePoster = currentFilm.getPoster(); // ✅ Get poster correctly

            if (dbHelper.isFavorite(idFilm)) {
                boolean removed = dbHelper.removeFavorite(idFilm);
                if (removed) {
                    updateFavoriteIcon(false);
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to remove movie", Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean added = dbHelper.addFavorite(idFilm, movieTitle, moviePoster);
                if (added) {
                    updateFavoriteIcon(true);
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add movie", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
// Update favorite icon state
private void updateFavoriteIcon(boolean isFavorite) {
    if (isFavorite) {
        favImg.setImageResource(R.drawable.fav_filled); // Change to filled heart icon
    } else {
        favImg.setImageResource(R.drawable.fav); // Change to empty heart icon
    }
}

    private void sendRequest(){
        mRequestQueue= Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        mStringRequest=new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + idFilm, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                currentFilm = gson.fromJson(response, FilmItem.class); // ✅ Store the movie details

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
            }

        }, error -> progressBar.setVisibility(View.GONE));
        mRequestQueue.add(mStringRequest);
    }

    private void initView(){
        titleTxt=findViewById(R.id.movieNameTxt);
        progressBar=findViewById(R.id.progressBarDetail);
        scrollView=findViewById(R.id.scrollView2);
        pic2=findViewById(R.id.picDetail);
        movieRateTxt=findViewById(R.id.movieStar);
        movieTimeTxt=findViewById(R.id.movieTime);
        movieSummaryInfo=findViewById(R.id.movieSummary);
        movieActorsInfo=findViewById(R.id.movieActorInfo);
        backImg=findViewById(R.id.backImg);
        recyclerViewCategory=findViewById(R.id.genreView);
        recyclerViewActors=findViewById(R.id.imagesRecycler);
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        backImg.setOnClickListener(v -> finish());

        favImg = findViewById(R.id.imageView8); // Ensure this ID matches the favorite icon in XML
        dbHelper = new FavoritesDatabaseHelper(this);
    }
}
