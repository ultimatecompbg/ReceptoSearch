package com.vikves.receptosearch;

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

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.OnRecipeClickListener {
    private RecyclerView recyclerView;
    private RecipeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecipeListAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fetchRecipes("pasta");
    }

    private void fetchRecipes(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeService service = retrofit.create(RecipeService.class);
        Call<RecipeResponse> call = service.getRecipes(query, "YOUR_SPOONACULAR_API_KEY");

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getRecipes();
                    adapter.setRecipes(recipes);
                } else {
                    Log.e("MainActivity", "Failed to fetch recipes");
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("MainActivity", "Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Handle recipe item click (e.g., open detail activity)
    }
}

