package com.vikves.receptosearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vikves.receptosearch.adapter.RecipeListAdapter;
import com.vikves.receptosearch.db.DBHelper;
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
    private DBHelper dbHelper;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);

        // Check if the user is logged in
        if (!dbHelper.isLoggedIn(this)) {
            // User is not logged in, redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.recipe_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                fetchRecipes(selectedOption.toLowerCase()); // Assuming API endpoints are lowercase
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }

        });
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            logout(); // Call logout method when button is clicked
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeListAdapter(new ArrayList<>(), this);
        adapter.setOnRecipeClickListener(this);
        recyclerView.setAdapter(adapter);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("https://api.spoonacular.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        recipeService = retrofit.create(RecipeService.class);

        fetchRecipes("pasta"); // Default to pasta recipes on startup

    }

    private void fetchRecipes(String query) {
        Call<RecipeResponse> call = recipeService.getRecipes(query, "09ad6edbb5944893801b2f32cb4af037");
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    RecipeResponse recipeResponse = response.body();
                    if (recipeResponse != null && recipeResponse.getResults() != null) {
                        List<Recipe> recipes = recipeResponse.getResults();
                        adapter.setRecipes(recipes);
                    } else {
                        Log.e(TAG, "Empty response or null recipes received");
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
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
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
    private void logout() {

        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

        // Add flags to clear the back stack and prevent the user from navigating back to MainActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
