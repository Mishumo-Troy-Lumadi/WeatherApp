package com.troy.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.troy.weatherapp.models.WeatherResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public String API_BASE_URL = "https://api.openweathermap.org/";

    TextView txtResponse;

    String TAG = "MainActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResponse = findViewById(R.id.txtResponce);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder();

        builder.baseUrl(API_BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient.build());

        Retrofit retrofit = builder.build();

        WeatherAPIClient client = retrofit.create(WeatherAPIClient.class);

        client.getWeather("Thohoyandou", getResources().getString(R.string.weather_API), "Metric")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        Toast.makeText(MainActivity.this, "Responded", Toast.LENGTH_SHORT).show();

                        if (response.body() == null) {
                            txtResponse.setText(response.toString());
                            Log.i(TAG, "onResponse: " + response.toString());
                        } else {

                            txtResponse.setText(response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        Log.i(TAG, "onFailure: " + t.toString());
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}