package com.vikves.receptosearch.response;

import com.google.gson.annotations.SerializedName;
import com.vikves.receptosearch.model.Recipe;

import java.util.List;

public class RecipeResponse {
    @SerializedName("results")
    private List<Recipe> results;

    public List<Recipe> getResults() {
        return results;
    }

    public void setResults(List<Recipe> results) {
        this.results = results;
    }
}
