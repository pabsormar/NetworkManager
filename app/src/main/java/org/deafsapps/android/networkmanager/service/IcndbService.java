package org.deafsapps.android.networkmanager.service;

import org.deafsapps.android.networkmanager.domain.JokeCategoriesResponse;
import org.deafsapps.android.networkmanager.domain.JokeItemResponse;
import org.deafsapps.android.networkmanager.domain.JokesCountResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IcndbService {

    @GET("jokes/random")
    Call<JokeItemResponse> fetchRandomJoke(@Query("firstName") String firstName, @Query("lastName") String lastName);

    @GET("jokes/count")
    Call<JokesCountResponse> fetchJokesCount();

    @GET("categories")
    Call<JokeCategoriesResponse> fetchJokeCategories();

}