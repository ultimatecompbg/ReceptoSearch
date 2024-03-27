package com.vikves.receptosearch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vikves.receptosearch.R;
import com.vikves.receptosearch.model.Recipe;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private OnRecipeClickListener listener;

    public RecipeListAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private ImageView thumbnailImageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
        }

        public void bind(final Recipe recipe, final OnRecipeClickListener listener) {
            titleTextView.setText(recipe.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }
}
