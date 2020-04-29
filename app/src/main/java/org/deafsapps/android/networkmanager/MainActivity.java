package org.deafsapps.android.networkmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.deafsapps.android.networkmanager.domain.JokeCategoriesResponse;
import org.deafsapps.android.networkmanager.domain.JokeItem;
import org.deafsapps.android.networkmanager.domain.JokeItemResponse;
import org.deafsapps.android.networkmanager.domain.JokesCountResponse;
import org.deafsapps.android.networkmanager.service.IcndbService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView tvJoke;
    private Thread workerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imgProfile = findViewById(R.id.activity_main__img__profile);

        tvJoke = findViewById(R.id.activity_main__tv__joke);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop and destroy any running worker thread
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the 'Menu' layout
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_db_info) {
            Log.i("Menu option", "Item: " + item.getTitle());
            requestJokeDbInformation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestJokeDbInformation() {
        // request configurations
        final Retrofit retrofitInstance = getIcndbRetrofitInstance();
        Call<JokesCountResponse> countCall = retrofitInstance.create(IcndbService.class).fetchJokesCount();
        Call<JokeCategoriesResponse> categoriesCall = retrofitInstance.create(IcndbService.class).fetchJokeCategories();
        // request invocations
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<JokesCountResponse> countResponse = countCall.execute();
                    Response<JokeCategoriesResponse> categoriesResponse = categoriesCall.execute();

                    if (countResponse.isSuccessful() && categoriesResponse.isSuccessful()) {
                        // show the info in a 'Dialog' or similar
                        displayDbInfoOnScreen(countResponse.body(), categoriesResponse.body());
                    } else {
                        // an error took place
                        Log.e("", "Not fine");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        workerThread.start();
    }

    private void displayDbInfoOnScreen(JokesCountResponse count, JokeCategoriesResponse categories) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = createIcndbInfoDialog(count, categories);
                dialogBuilder.show();
            }
        });
    }

    private AlertDialog.Builder createIcndbInfoDialog(JokesCountResponse count, JokeCategoriesResponse categories) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle("ICNDB Info");
        dialogBuilder.setMessage("The ICNDB comprises " + count.getJokesCount()
                + " jokes and " + categories.getCategories().length + " categories");
        dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return dialogBuilder;
    }

    private void requestRandomJokeWithReplacedNameFromService(String firstName, String lastName) {
        // request configuration
        final Retrofit retrofitInstance = getIcndbRetrofitInstance();
        Call<JokeItemResponse> randomJokeCall = retrofitInstance.create(IcndbService.class).fetchRandomJoke(firstName, lastName);
        // request invocation asynchronously
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

    private Retrofit getIcndbRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl("http://api.icndb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void displayError(String errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
    }

    private void loadDataIntoView(JokeItem jokeItem) {
        Toast.makeText(this, jokeItem.getJoke(), Toast.LENGTH_LONG).show();
        tvJoke.setText(jokeItem.getJoke());
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
