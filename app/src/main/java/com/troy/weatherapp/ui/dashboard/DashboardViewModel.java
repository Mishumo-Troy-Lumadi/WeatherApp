package com.troy.weatherapp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.troy.weatherapp.models.WeatherResponse;

import java.util.ArrayList;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<WeatherResponse>> arWeather;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        arWeather = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<WeatherResponse>> getArWeather() {
        return arWeather;
    }

    public void setArWeather(MutableLiveData<ArrayList<WeatherResponse>> arWeather) {
        this.arWeather = arWeather;
    }

    public LiveData<String> getText() {
        return mText;
    }
}