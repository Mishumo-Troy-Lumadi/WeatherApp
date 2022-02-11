package com.troy.weatherapp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tapadoo.alerter.Alerter;
import com.troy.weatherapp.HomeActivity;
import com.troy.weatherapp.R;
import com.troy.weatherapp.models.WeatherResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WeatherWidgetAdapter extends RecyclerView.Adapter<WeatherWidgetAdapter.WeatherHolder> {

    ArrayList<WeatherResponse> arWeather;
    Context context;
    Activity activity;

    public WeatherWidgetAdapter(Activity activity, Context context, ArrayList<WeatherResponse> arWeather) {
        this.arWeather = arWeather;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_list_widget, parent, false);
        return new WeatherHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull WeatherHolder holder, int position) {

        WeatherResponse response = arWeather.get(position);

        holder.txtCity.setText(response.getName());
        holder.txtTemp.setText(Math.round(response.getMain().getTemp()) + HomeActivity.DEGREE);
        holder.txtDesc.setText(response.getWeather().get(0).getDescription());

        String mode = "d", icon = response.getWeather().get(0).getIcon();

        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;


        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                mode = "n";
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                mode = "d";
                break;
        }

        icon = icon.substring(0, 2) + mode;
        String url = HomeActivity.ICON_URL + icon + HomeActivity.ICON_END;

        Glide.with(context).load(url).into(holder.imgIcon);

        holder.body.setOnClickListener(view -> Alerter.create(activity)
                .setTitle("The weather in " + response.getName())
                .setText(response.getMain().getTemp() + HomeActivity.DEGREE +
                        " with " + response.getWeather().get(0).getDescription())
                .setBackgroundColorRes(R.color.default_color)
                .setProgressColorRes(R.color.white)
                .show());

    }

    @Override
    public int getItemCount() {
        return arWeather.size();
    }

    public static class WeatherHolder extends RecyclerView.ViewHolder {

        CardView body;
        TextView txtTemp, txtCity, txtDesc;
        ImageView imgIcon;

        public WeatherHolder(@NonNull View itemView) {

            super(itemView);

            body = itemView.findViewById(R.id.cardBody);

            txtTemp = itemView.findViewById(R.id.txtWidTemp);
            txtCity = itemView.findViewById(R.id.txtWidCity);
            txtDesc = itemView.findViewById(R.id.txtWidDesc);

            imgIcon = itemView.findViewById(R.id.imgWidIcon);

        }

    }
}
