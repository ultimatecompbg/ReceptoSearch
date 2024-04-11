package com.vikves.receptosearch;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.vikves.receptosearch.model.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Retrieve recipe details from intent extras
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("recipe")) {
            Recipe recipe = intent.getParcelableExtra("recipe");

            // Display recipe details in the UI
            if (recipe != null) {
                setTitle(recipe.getTitle()); // Set activity title

                // Update TextViews with recipe details
                TextView titleTextView = findViewById(R.id.titleTextView);
                titleTextView.setText(recipe.getTitle());
                // Update ImageView with recipe image using Picasso
                ImageView imageView = findViewById(R.id.recipeImageView);
                Picasso.get().load(recipe.getImageUrl()).into(imageView);
            }
        } else {
            // Handle case where intent extras are missing
            Toast.makeText(this, "Recipe details not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }

        // Enable back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle back button click
        onBackPressed();
        return true;
    }
}
