package com.vikves.receptosearch;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vikves.receptosearch.adapter.RecipeListAdapter;
import com.vikves.receptosearch.model.Recipe;
import com.vikves.receptosearch.response.RecipeResponse;
import com.vikves.receptosearch.service.RecipeService;

import java.util.ArrayList;
import java.util.List;
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

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of the RecipeService interface
        recipeService = retrofit.create(RecipeService.class);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeListAdapter(new ArrayList<>(), this);
        adapter.setOnRecipeClickListener(this); // Set the click listener
        recyclerView.setAdapter(adapter);

        // Fetch recipes from Spoonacular API
        fetchRecipes("pasta");
    }

    private void fetchRecipes(String query) {
        Call<RecipeResponse> call = recipeService.getRecipes(query, "09ad6edbb5944893801b2f32cb4af037");
        call.enqueue(new ApiResponseCallback<RecipeResponse>() {
            @Override
            public void onResponse(RecipeResponse response) {
                if (response != null && response.getRecipes() != null) {
                    List<Recipe> recipes = response.getRecipes();
                    adapter.setRecipes(recipes);
                } else {
                    Log.e(TAG, "Empty response or null recipes received");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to fetch recipes: " + errorMessage);
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
