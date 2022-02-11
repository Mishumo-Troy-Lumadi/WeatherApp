package com.troy.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.tapadoo.alerter.Alerter;
import com.troy.weatherapp.databinding.ActivityHomeBinding;
import com.troy.weatherapp.models.WeatherResponse;
import com.troy.weatherapp.ui.dashboard.DashboardViewModel;
import com.troy.weatherapp.ui.home.HomeFragment;
import com.troy.weatherapp.ui.home.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    public static String ICON_URL = "https://openweathermap.org/img/wn/";
    public static String ICON_END = "@2x.png";
    public static String API_BASE_URL = "https://api.openweathermap.org/";
    public static String DEGREE = "°";
    public static HomeViewModel homeViewModel;
    public static DashboardViewModel dashboardViewModel;

    @SuppressLint("StaticFieldLeak")
    private static Context c;
    public Context cs;

    public static double lat = 0, lon = 0;

    @SuppressLint("StaticFieldLeak")
    static Activity activity;
    @SuppressLint("StaticFieldLeak")
    static FusedLocationProviderClient fusedLocationProviderClient;

    static String API;

    static String[] locations = {"Musina", "Sibasa", "Midrand", "Vanderbijlpark", "Bloemfontein","Capetown","Durban","Pretoria","Centurion","Kimberly"};

    public static ArrayList<WeatherResponse> arWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        arWeather = new ArrayList<>();
        API = getResources().getString(R.string.weather_API);

        activity = this;
        c = getApplicationContext();
        cs = getApplicationContext();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(c);


        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        com.troy.weatherapp.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);

        NavigationUI.setupWithNavController(binding.navView, navController);


        if (c.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Alerter.create(activity)
                    .setTitle("Permissions Required")
                    .setText("Requesting Access to location")
                    .setBackgroundColorRes(R.color.default_color)
                    .setProgressColorRes(R.color.white)
                    .show();

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionInfo.PROTECTION_SIGNATURE);

        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {

                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();

                    new LatLng(lat, lon);

                    loadData();
                    loadDataArray();
                }

            });
        }


    }

    public static int reload = 0;

    public static void loadData() {
        String TAG = "HomeActivity: LoadData:";
       // Log.i(TAG, "loadData: loading data from api :" + reload);
        reload++;

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Log.i(TAG, "loadData: Not Connected To Internet");
            Alerter.create(activity)
                    .setTitle("Network Error")
                    .setText("Oops Switch on mobile data or connect to WIFI...")
                    .disableOutsideTouch()
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.red)
                    .setProgressColorRes(R.color.white)
                    .show();
            return;
        }


        if (reload > 5) {
            Log.i(TAG, "loadData: reloaded more than 5 times");
            Alerter.create(activity)
                    .setTitle("Error")
                    .setText("Oops Could not refresh data...")
                    .disableOutsideTouch()
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.red)
                    .setProgressColorRes(R.color.white)
                    .show();
            return;
        }

        Log.i(TAG, "loadData: loading");
        Alerter.create(activity)
                .setTitle("Loading")
                .setText("Loading Data...")
                .enableProgress(true)
                .disableOutsideTouch()
                .setBackgroundColorRes(R.color.default_color)
                .setProgressColorRes(R.color.white)
                .show();


        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Alerter.create(activity)
                    .setTitle("Permissions Required")
                    .setText("Requesting Access to location")
                    .setBackgroundColorRes(R.color.default_color)
                    .setProgressColorRes(R.color.white)
                    .show();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionInfo.PROTECTION_SIGNATURE);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            Log.i(TAG, "loadData: getting device location");
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                new LatLng(lat, lon);


            }

        });

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder();

        builder.baseUrl(API_BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient.build());

        Retrofit retrofit = builder.build();

        WeatherAPIClient client = retrofit.create(WeatherAPIClient.class);
        Log.i(TAG, "loadData: retrieving data from api");
        if (lat != 0 && lon != 0) {
            reload = 0;
            client.getWeather(lat, lon, API, "Metric")
                    .enqueue(new Callback<WeatherResponse>() {


                        @Override
                        public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                            //Toast.makeText(HomeActivity.this, "Responded", Toast.LENGTH_SHORT).show();

                            if (response.body() == null) {
                                Log.i(TAG, "onResponse: " + response.toString());

                            } else {
                                //Log.i(TAG, "onResponse: " + response.body().toString());
                                homeViewModel.setWeatherResponseMutableLiveData(new MutableLiveData<>(response.body()));
                                HomeFragment.setWeather(response.body());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                            Log.e(TAG, "onFailure: " + t.toString());
                            //Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            Alerter.create(activity)
                                    .setTitle("Error")
                                    .setText("Could not fetch data...\n" + t.toString())
                                    .disableOutsideTouch()
                                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(R.color.red)
                                    .setProgressColorRes(R.color.white)
                                    .show();
                        }
                    });
        } else {
            loadData();
        }
    }

    static boolean done = false;
    static int i = 0;

    public static void loadDataArray() {
        String TAG = "HomeActivity: LoadDataArray:";
        arWeather = new ArrayList<>();
        Log.i(TAG, "loadData: loading data from api ");

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Alerter.create(activity)
                    .setTitle("Network Error")
                    .setText("Oops Switch on mobile data or connect to WIFI...")
                    .disableOutsideTouch()
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.red)
                    .setProgressColorRes(R.color.white)
                    .show();
            return;
        }

        Alerter.create(activity)
                .setTitle("Loading")
                .setText("Loading Data...")
                .enableProgress(true)
                .disableOutsideTouch()
                .setBackgroundColorRes(R.color.default_color)
                .setProgressColorRes(R.color.white)
                .show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder();

        builder.baseUrl(API_BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient.build());

        Retrofit retrofit = builder.build();

        WeatherAPIClient client = retrofit.create(WeatherAPIClient.class);
        @SuppressWarnings("rawtypes") AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {


                for (i = 0; i < locations.length; i++) {

                    String city = locations[i];

                    boolean exist = false;
                    for (WeatherResponse resp : arWeather) {
                        if (resp.getName().equals(city)) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist) {

                        Log.d(TAG, "getWeather() called with: city = " + city + "");

                        client.getWeather(city, API, "Metric")
                                .enqueue(new Callback<WeatherResponse>() {

                                    @Override
                                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                                        if (response.body() == null) {
                                            Log.i(TAG, "onFailure: " + response.toString());

                                        } else {
                                           // Log.i(TAG, "onResponse: " + response.body().toString());
                                            arWeather.add(response.body());
                                            dashboardViewModel.setArWeather(new MutableLiveData<>(arWeather));
                                            if (i == locations.length - 1) {
                                                Log.i(TAG, "loadDataArray: done fetching from api");
                                                done = true;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.toString());

                                        Alerter.create(activity)
                                                .setTitle("Error")
                                                .setText("Could not fetch data for " + city)
                                                .disableOutsideTouch()
                                                .setIcon(R.drawable.ic_baseline_error_outline_24)
                                                .enableSwipeToDismiss()
                                                .setBackgroundColorRes(R.color.red)
                                                .setProgressColorRes(R.color.white)
                                                .show();
                                    }
                                });
                    }

                }

                return done;
            }

        };

        task.execute();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionInfo.PROTECTION_SIGNATURE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
                loadDataArray();
            } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                loadData();
                loadDataArray();
            } else if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                loadData();
                loadDataArray();
            }
        }

    }


}

