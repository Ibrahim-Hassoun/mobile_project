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

        ImageView favBtn = findViewById(R.id.imageView4);
        favBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }

    private void sendRequestBestMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);

        mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=1",
                response -> {
                    Gson gson = new Gson();
                    loading1.setVisibility(View.GONE);

                    // Convert ListFilm to ArrayList<Datum>
                    ListFilm items = gson.fromJson(response, ListFilm.class);
                    ArrayList<Datum> moviesList = new ArrayList<>(items.getData()); // ✅ Fix

                    adapterBestMovies = new FilmListAdapter(this, moviesList); // ✅ Pass correct arguments
                    recyclerViewBestMovies.setAdapter(adapterBestMovies);
                },
                error -> {
                    loading1.setVisibility(View.GONE);
                    Log.i("UiLover", "onErrorResponse: " + error.toString());
                });

        mRequestQueue.add(mStringRequest);
    }


    private void sendRequestUpComing() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading3.setVisibility(View.VISIBLE);

        mStringRequest3 = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=2",
                response -> {
                    Gson gson = new Gson();
                    loading3.setVisibility(View.GONE);

                    // Convert ListFilm to ArrayList<Datum>
                    ListFilm items = gson.fromJson(response, ListFilm.class);
                    ArrayList<Datum> moviesList = new ArrayList<>(items.getData()); // ✅ Fix

                    adapterUpComing = new FilmListAdapter(this, moviesList); // ✅ Pass correct arguments
                    recyclerviewUpcoming.setAdapter(adapterUpComing);
                },
                error -> {
                    loading3.setVisibility(View.GONE);
                    Log.i("UiLover", "onErrorResponse: " + error.toString());
                });

        mRequestQueue.add(mStringRequest3);
    }


    private void sendRequestCategory(){
        mRequestQueue= Volley.newRequestQueue(this);
        loading2.setVisibility(View.VISIBLE);
        mStringRequest2=new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/genres", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson=new Gson();
                loading2.setVisibility(View.GONE);
                ArrayList<GenresItem> catList=gson.fromJson(response,new TypeToken<ArrayList<GenresItem>>(){}.getType());
                adapterCategory=new CategoryListAdapter(catList);
                recyclerviewCategory.setAdapter(adapterCategory);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                loading2.setVisibility(View.GONE);
                Log.i("UiLover","onErrorResponse: "+ error.toString());
                //Log.e("Error", "Error fetching upcoming movies: " + error.getMessage());
            }
        });
        mRequestQueue.add(mStringRequest2);
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

    }
}
