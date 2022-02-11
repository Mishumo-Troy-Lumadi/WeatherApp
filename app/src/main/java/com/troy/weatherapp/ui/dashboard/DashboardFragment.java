package com.troy.weatherapp.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.troy.weatherapp.HomeActivity;
import com.troy.weatherapp.databinding.FragmentDashboardBinding;
import com.troy.weatherapp.models.WeatherResponse;
import com.troy.weatherapp.ui.WeatherWidgetAdapter;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    public static ArrayList<WeatherResponse> arWeather;
    public static Context context;
    static Activity activity;

    static String TAG = "DashBoard ";

    private FragmentDashboardBinding binding;

    DashboardViewModel dashboardViewModel;
    private static RecyclerView recWidWeather;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = HomeActivity.dashboardViewModel;
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = getContext();
        recWidWeather = binding.recWidWeather;
        activity = getActivity();

        WeatherWidgetAdapter adapter = new WeatherWidgetAdapter(activity, context, dashboardViewModel.getArWeather().getValue());
        recWidWeather.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recWidWeather.setAdapter(adapter);

        // Log.i(TAG, "onCreateView: ARRAY" + dashboardViewModel.getArWeather().getValue());
        dashboardViewModel.getArWeather().observe(getViewLifecycleOwner(), DashboardFragment::loadRecyclerView);

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        dashboardViewModel.getArWeather().observe(getViewLifecycleOwner(), DashboardFragment::loadRecyclerView);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //dashboardViewModel.getArWeather().observe(getViewLifecycleOwner(), DashboardFragment::loadRecyclerView);
        //Log.i(TAG, "onViewCreated: " + dashboardViewModel.getArWeather().getValue());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public static void loadRecyclerView(ArrayList<WeatherResponse> responseArrayList) {

        if (responseArrayList != null) {
            arWeather = responseArrayList;
            //Log.d(TAG, "loadRecyclerView() called with: responseArrayList = [" + responseArrayList + "]");
            WeatherWidgetAdapter adapter = new WeatherWidgetAdapter(activity, context, responseArrayList);
            recWidWeather.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            recWidWeather.setAdapter(adapter);
        } else {
            HomeActivity.loadDataArray();
        }

    }
}