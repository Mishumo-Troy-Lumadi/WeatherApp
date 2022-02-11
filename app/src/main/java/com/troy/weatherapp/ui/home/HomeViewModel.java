package com.troy.weatherapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.troy.weatherapp.models.WeatherResponse;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<WeatherResponse> weatherResponseMutableLiveData;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        weatherResponseMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<WeatherResponse> getWeatherResponseMutableLiveData() {
        return weatherResponseMutableLiveData;
    }

    public void setWeatherResponseMutableLiveData(MutableLiveData<WeatherResponse> weatherResponseMutableLiveData) {
        this.weatherResponseMutableLiveData = weatherResponseMutableLiveData;
    }

    public void setText(MutableLiveData<String> mText) {
        this.mText = mText;
    }

    public LiveData<String> getText() {
        return mText;
    }
}