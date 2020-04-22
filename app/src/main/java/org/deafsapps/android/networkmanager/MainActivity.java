package org.deafsapps.android.networkmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

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
