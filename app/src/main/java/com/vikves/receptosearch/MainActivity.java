package com.vikves.receptosearch;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vikves.receptosearch.adapter.RecipeListAdapter;
import com.vikves.receptosearch.model.Recipe;
import com.vikves.receptosearch.response.RecipeResponse;
import com.vikves.receptosearch.service.RecipeService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity implements RecipeListAdapter.OnRecipeClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecipeListAdapter adapter;
    private RecipeService recipeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeListAdapter(new ArrayList<>(), this);
        adapter.setOnRecipeClickListener(this); // Set the click listener
        recyclerView.setAdapter(adapter);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

// Add OkHttp logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("https://api.spoonacular.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Initialize Retrofit

        // Create an instance of the RecipeService interface
        recipeService = retrofit.create(RecipeService.class);

        // Initialize RecyclerView


        // Fetch recipes from Spoonacular API
        fetchRecipes("pasta");
    }

    private void fetchRecipes(String query) {
        Call<RecipeResponse> call = recipeService.getRecipes(query, "09ad6edbb5944893801b2f32cb4af037");
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    RecipeResponse recipeResponse = response.body();
                    if(recipeResponse.getResults() == null){
                        Log.e(TAG, "Response is null");
                    }else {
                        List<Recipe> recipes = recipeResponse.getResults();
                        adapter.setRecipes(recipes);
                    }

                } else {
                    Log.e(TAG, "Empty response or null recipes received");
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e(TAG, "Error getting response: " + t.getMessage());
            }

        });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Handle recipe item click (e.g., open detail activity)
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}
