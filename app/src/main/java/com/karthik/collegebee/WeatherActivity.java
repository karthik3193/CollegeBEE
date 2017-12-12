package com.karthik.collegebee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.kwabenaberko.openweathermaplib.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.kwabenaberko.openweathermaplib.models.threehourforecast.ThreeHourForecast;

import net.kjulio.rxlocation.RxLocation;

import io.reactivex.functions.Consumer;

public class WeatherActivity extends AppCompatActivity {
    OpenWeatherMapHelper helper = new OpenWeatherMapHelper();
    double lat,lng;
    LocationRequest locationRequest;
    ProgressDialog progressDialog;
    Button cw,fw;
    TextView editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        helper.setApiKey(getString(R.string.OPEN_WEATHER_MAP_API_KEY));
        helper.setUnits(Units.IMPERIAL);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);
        LocationManager manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            new AlertDialog.Builder(this)
                    .setTitle("ENABLE GPS!!!")
                    .setMessage("GPS SERVICES disabled, please enable GPS.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }

        cw = (Button) findViewById(R.id.cw);
        editText=(TextView) findViewById(R.id.et);
        cw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProgress();
                LocateMe();
                helper.getCurrentWeatherByGeoCoordinates(lat, lng, new OpenWeatherMapHelper.CurrentWeatherCallback() {
                    @Override
                    public void onSuccess(CurrentWeather currentWeather) {

                        editText.setText(
                                "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
                                        +"Weather Description: " + currentWeather.getWeatherArray().get(0).getDescription() + "\n"
                                        +"Max Temperature: " + currentWeather.getMain().getTempMax()+"\n"
                                        +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                                        +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        editText.setText( throwable.getMessage());
                    }
                });

            }
        });
         fw= (Button) findViewById(R.id.fw);
         fw.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 displayProgress();
                 LocateMe();
                 helper.getThreeHourForecastByGeoCoordinates(lat,lng, new OpenWeatherMapHelper.ThreeHourForecastCallback() {
                     @Override
                     public void onSuccess(ThreeHourForecast threeHourForecast) {
                         editText.setText(
                                 "City/Country: "+ threeHourForecast.getCity().getName() + "/" + threeHourForecast.getCity().getCountry() +"\n"
                                         +"Forecast Array Count: " + threeHourForecast.getCnt() +"\n"
                                         //For this example, we are logging details of only the first forecast object in the forecasts array
                                         +"First Forecast Date Timestamp: " + threeHourForecast.getThreeHourWeatherArray().get(0).getDt() +"\n"
                                         +"First Forecast Weather Description: " + threeHourForecast.getThreeHourWeatherArray().get(0).getWeatherArray().get(0).getDescription()+ "\n"
                                         +"First Forecast Max Temperature: " + threeHourForecast.getThreeHourWeatherArray().get(0).getMain().getTempMax()+"\n"
                                         +"First Forecast Wind Speed: " + threeHourForecast.getThreeHourWeatherArray().get(0).getWind().getSpeed() + "\n"
                         );
                     }

                     @Override
                     public void onFailure(Throwable throwable) {
                         editText.setText( throwable.getMessage());
                     }
                 });
             }
         });

    }
    public void LocateMe(){
        RxLocation.locationUpdates(getApplicationContext(), locationRequest)
                .firstElement()
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
                        lat= location.getLatitude();
                        lng= location.getLongitude();
                        dismissProgress();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getApplicationContext(),throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }


    @Override
    protected void onPause() {
        super.onPause();

        dismissProgress();
    }

    private void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage("Getting location...");
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
