package com.vikves.receptosearch;

import retrofit2.Call;
import retrofit2.Response;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiResponseCallback<T> implements Callback<T> {

    public abstract void onResponse(T response);

    public abstract void onFailure(String errorMessage);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onResponse(response.body());
        } else {
            onFailure("Failed to get response: " + response.code());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure("Network error: " + t.getMessage());
    }
}
