package com.example.weartv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weartv.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    CatImages catImagesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.cat_image_view);
        progressBar = findViewById(R.id.progress_bar);

        catImagesTask = new CatImages();
        catImagesTask.execute();
    }

    private class CatImages extends AsyncTask<String, Integer, String> {

        Bitmap currentCatPicture;

        @Override
        protected String doInBackground(String... strings) {

            while (true) {
                HttpURLConnection connection = null;
                try {
                    // Fetch image URL
                    URL url = new URL("https://cataas.com/cat?json=true");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    // Check for connection
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream input = connection.getInputStream();
                        currentCatPicture = BitmapFactory.decodeStream(input);


                        // Simulate progress and update UI
                        for (int i = 0; i < 100; i++) {
                            publishProgress(i);
                            Thread.sleep(30);
                        }

                    } else {
                        Log.e("CatImages", "Error fetching image: " + connection.getResponseMessage());
                        return "Error fetching image";
                    }

                } catch (Exception e) {
                    Log.e("CatImages", "Error: " + e.getMessage());
                    return "Error fetching or processing image";
                } finally {
                    connection.disconnect(); // Close connection
                }

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            if (currentCatPicture != null) {
                imageView.setImageBitmap(currentCatPicture);
                currentCatPicture = null; // Reset for the next image
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(MainActivity.this, result  , Toast.LENGTH_SHORT).show();
            }
        }
    }
}
