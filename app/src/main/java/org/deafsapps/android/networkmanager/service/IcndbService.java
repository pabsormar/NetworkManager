package org.deafsapps.android.networkmanager.service;

import org.deafsapps.android.networkmanager.domain.JokeItemResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IcndbService {

    @GET("jokes/random")
    Call<JokeItemResponse> fetchRandomJoke();

}