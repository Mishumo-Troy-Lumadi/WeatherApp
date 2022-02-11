package com.troy.weatherapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.troy.weatherapp.HomeActivity;
import com.troy.weatherapp.databinding.FragmentHomeBinding;
import com.troy.weatherapp.models.WeatherResponse;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    static Context c;

    private static HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    @SuppressLint("StaticFieldLeak")
    private static TextView txtTemp;
    @SuppressLint("StaticFieldLeak")
    private static TextView txtDesc;
    @SuppressLint("StaticFieldLeak")
    private static TextView txtMinMax;
    @SuppressLint("StaticFieldLeak")
    private static TextView txtCity;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imgIcon;
    @SuppressLint("StaticFieldLeak")
    private static ImageButton btnRefresh;
    @SuppressLint("StaticFieldLeak")
    private static TextView txtFeelsLike;

    //static String TAG = "HomeFragment";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = HomeActivity.homeViewModel;
        homeViewModel.getText();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        txtCity = binding.txtCity;
        txtFeelsLike = binding.tvFeelsLike;
        txtDesc = binding.txtDesc;
        txtMinMax = binding.txtMinMax;
        txtTemp = binding.txtTemp;

        imgIcon = binding.imgIcon;

        btnRefresh = binding.btnRefreshTemp;


        btnRefresh.setOnClickListener(view -> {
            HomeActivity.reload = 0;
            HomeActivity.loadData();
            HomeActivity.loadDataArray();
        });

        c = getContext();

        return root;
    }


    @SuppressLint("SetTextI18n")
    public static void setWeather(WeatherResponse response) {
       // Log.i(TAG, "setWeather: from :" +response);
        String mode = "d", icon = response.getWeather().get(0).getIcon();

        int nightModeFlags = c.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        //Toast.makeText(c, "flag: " + nightModeFlags, Toast.LENGTH_SHORT).show();

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                mode = "n";
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                mode = "d";
                break;
        }

        //Toast.makeText(c, "mode:" + mode, Toast.LENGTH_SHORT).show();


        icon = icon.substring(0, 2) + mode;

        double min, max;

        min = response.getMain().getTempMin();
        max = response.getMain().getTempMax();

        txtCity.setText(response.getName());
        txtTemp.setText(response.getMain().getTemp() + HomeActivity.DEGREE);
        txtFeelsLike.setText("Feels Like " + response.getMain().getFeelsLike() + HomeActivity.DEGREE);
        txtMinMax.setText("min " + min + HomeActivity.DEGREE + " / max " + max + HomeActivity.DEGREE);
        txtDesc.setText(response.getWeather().get(0).getDescription());

        String url = HomeActivity.ICON_URL + icon + HomeActivity.ICON_END;

        Glide.with(c).load(url).into(imgIcon);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel.getWeatherResponseMutableLiveData().observe(getViewLifecycleOwner(), HomeFragment::setWeather);


    }
}