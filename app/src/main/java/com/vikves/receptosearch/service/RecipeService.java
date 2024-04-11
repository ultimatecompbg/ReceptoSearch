package com.vikves.receptosearch.service;

import com.vikves.receptosearch.response.RecipeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeService {
    @GET("recipes/complexSearch")
    Call<RecipeResponse> getRecipes(
            @Query("query") String query,
            @Query("apiKey") String apiKey



    );
}
