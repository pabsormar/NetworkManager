package org.deafsapps.android.networkmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.deafsapps.android.networkmanager.domain.JokeItem;
import org.deafsapps.android.networkmanager.domain.JokeItemResponse;
import org.deafsapps.android.networkmanager.service.IcndbService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imgProfile = findViewById(R.id.activity_main__img__profile);

        final Button btnLoadImage = findViewById(R.id.activity_main__btn__load_image);
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImageIntoImageView(imgProfile);
            }
        });

        final Button btnLoadData = findViewById(R.id.activity_main__btn__load_data);
        btnLoadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = "John";
                String lastName = "Bloggs";
                requestRandomJokeWithReplacedNameFromService(firstName, lastName);
            }
        });
    }

    private void requestRandomJokeWithReplacedNameFromService(String firstName, String lastName) {
        // request configuration
        Retrofit retrofitInstance = new Retrofit.Builder()
                .baseUrl("http://api.icndb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Call<JokeItemResponse> randomJokeCall = retrofitInstance.create(IcndbService.class).fetchRandomJoke();
        // request invocation
        randomJokeCall.enqueue(new Callback<JokeItemResponse>() {
            @Override
            public void onResponse(Call<JokeItemResponse> call, Response<JokeItemResponse> response) {
                if (response.isSuccessful()) {
                    loadDataIntoView(response.body().getJokeItem());
                } else {
                    displayError(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<JokeItemResponse> call, Throwable t) {
                displayError("Error when fetching data");
                t.printStackTrace();
            }
        });
    }

    private void displayError(String errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
    }

    private void loadDataIntoView(JokeItem jokeItem) {
        Toast.makeText(this, jokeItem.getJoke(), Toast.LENGTH_LONG).show();
    }

    private void loadImageIntoImageView(ImageView imageView) {
        Glide.with(this)
                .load("https://assets.imgix.net/examples/vista_w900.png")
                .centerInside()
                .placeholder(R.drawable.ic_ph_image)
                .into(imageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkConnectionAndDisplayInfo();
    }

    private void checkNetworkConnectionAndDisplayInfo() {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

//        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
//        });

        String networkState;
        networkState = deprecatedCheckNetworkAndDisplay(cm);

        Toast.makeText(this, networkState, Toast.LENGTH_SHORT).show();
    }

    private String deprecatedCheckNetworkAndDisplay(ConnectivityManager cm) {
        // 1st approach
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }

        String networkState;
        if (networkInfo != null && networkInfo.isAvailable()) {
            networkState = "Connection OK";
        } else {
            networkState = "Something went wrong!";
        }
        return networkState;
    }

}
