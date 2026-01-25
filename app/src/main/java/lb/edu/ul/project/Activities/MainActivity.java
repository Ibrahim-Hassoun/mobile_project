package lb.edu.ul.project.Activities;

import android.content.Intent;
//import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
//import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.project.Adapters.CategoryListAdapter;
import lb.edu.ul.project.Adapters.FilmListAdapter;
import lb.edu.ul.project.Adapters.SliderAdapters;
import lb.edu.ul.project.Domain.Datum;
import lb.edu.ul.project.Domain.GenresItem;
import lb.edu.ul.project.Domain.ListFilm;
import lb.edu.ul.project.Domain.SliderItems;
import lb.edu.ul.project.R;



public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterBestMovies,adapterUpComing,adapterCategory;
    private RecyclerView recyclerViewBestMovies,recyclerviewUpcoming,recyclerviewCategory;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest,mStringRequest2,mStringRequest3;
    private ProgressBar loading1,loading2,loading3;
    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        initView();
        banners();
        sendRequestBestMovies();
        sendRequestUpComing();
        sendRequestCategory();
    }

    private void sendRequestBestMovies() {
        loading1.setVisibility(View.VISIBLE);
        
        try {
            String json = loadJSONFromAsset("movies_page1.json");
            Gson gson = new Gson();
            ListFilm items = gson.fromJson(json, ListFilm.class);
            ArrayList<Datum> moviesList = new ArrayList<>(items.getData());
            
            adapterBestMovies = new FilmListAdapter(this, moviesList);
            recyclerViewBestMovies.setAdapter(adapterBestMovies);
            loading1.setVisibility(View.GONE);
        } catch (Exception e) {
            loading1.setVisibility(View.GONE);
            Log.e("MainActivity", "Error loading movies: " + e.getMessage());
        }
    }


    private void sendRequestUpComing() {
        loading3.setVisibility(View.VISIBLE);
        
        try {
            String json = loadJSONFromAsset("movies_page2.json");
            Gson gson = new Gson();
            ListFilm items = gson.fromJson(json, ListFilm.class);
            ArrayList<Datum> moviesList = new ArrayList<>(items.getData());
            
            adapterUpComing = new FilmListAdapter(this, moviesList);
            recyclerviewUpcoming.setAdapter(adapterUpComing);
            loading3.setVisibility(View.GONE);
        } catch (Exception e) {
            loading3.setVisibility(View.GONE);
            Log.e("MainActivity", "Error loading movies: " + e.getMessage());
        }
    }


    private void sendRequestCategory(){
        loading2.setVisibility(View.VISIBLE);
        
        try {
            String json = loadJSONFromAsset("genres.json");
            Gson gson = new Gson();
            ArrayList<GenresItem> catList = gson.fromJson(json, new TypeToken<ArrayList<GenresItem>>(){}.getType());
            
            adapterCategory = new CategoryListAdapter(catList);
            recyclerviewCategory.setAdapter(adapterCategory);
            loading2.setVisibility(View.GONE);
        } catch (Exception e) {
            loading2.setVisibility(View.GONE);
            Log.e("MainActivity", "Error loading genres: " + e.getMessage());
        }
    }

    private void banners(){
        List<SliderItems> sliderItems=new ArrayList<>();
        sliderItems.add((new SliderItems(R.drawable.wide)));
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide3));

        viewPager2.setAdapter(new SliderAdapters(sliderItems,viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r=1-Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });

        viewPager2 .setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
            }
        });
    }
    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume(){
        super.onResume();
        slideHandler.postDelayed(sliderRunnable,2000);
    }

    private void initView(){
        viewPager2=findViewById(R.id.viewpagerSlider);
        recyclerViewBestMovies=findViewById(R.id.view1);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerviewUpcoming=findViewById(R.id.view3);
        recyclerviewUpcoming.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerviewCategory=findViewById(R.id.view2);
        recyclerviewCategory.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        loading1=findViewById(R.id.progressBar1);
        loading2=findViewById(R.id.progressBar2);
        loading3=findViewById(R.id.progressBar3);

        ImageView favBtn = findViewById(R.id.imageView4);
        favBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FavoritesActivity.class)));

        ImageView watchlistBtn = findViewById(R.id.imageView5);
        watchlistBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WatchlistActivity.class)));

        ImageView quizBtn = findViewById(R.id.imageView7);
        quizBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuizActivity.class)));

        ImageView profileBtn = findViewById(R.id.imageView6);
        profileBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

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
