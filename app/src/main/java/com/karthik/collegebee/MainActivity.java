package com.karthik.collegebee;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

public class MainActivity extends AppCompatActivity {
    CircleMenu circleMenu;
    Vibrator vibrator;
    Intent intent;
    TextView tv;
    String str;
    CircleMenuButton circleMenu1, circleMenu2, circleMenu3, circleMenu4, circleMenu5, circleMenu6, circleMenu7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.ltxt);
        str=(String) getIntent().getExtras().getString("mail");
        if(!str.equals(null))
        tv.setText(str);
        circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {

                    @Override
                    public void onItemClick(CircleMenuButton menuButton) {

                                if (menuButton.getHintText().equalsIgnoreCase("track me")) {
                                    vibrator.vibrate(30);
                                    intent = (Intent) new Intent(getApplicationContext(), Tracker.class);
                                }

                                if (menuButton.getHintText().equalsIgnoreCase("calculator")) {
                                    vibrator.vibrate(30);
                                    intent = (Intent) new Intent(getApplicationContext(), Calciulator.class);
                                }

                                if (menuButton.getHintText().equalsIgnoreCase("calendar")) {
                                    vibrator.vibrate(30);
                                    intent = (Intent) new Intent(getApplicationContext(), Calender.class);
                                }

                                if (menuButton.getHintText().equalsIgnoreCase("weather")) {
                                    vibrator.vibrate(30);
                                    intent = (Intent) new Intent(getApplicationContext(), WeatherActivity.class);
                                }
                        if (menuButton.getHintText().equalsIgnoreCase("my account")) {
                            vibrator.vibrate(30);
                            intent = (Intent) new Intent(getApplicationContext(), TripLauncher.class);
                        }
                        if (menuButton.getHintText().equalsIgnoreCase("about")) {
                            vibrator.vibrate(30);
                            intent = (Intent) new Intent(getApplicationContext(), About.class);
                        }
                        if (menuButton.getHintText().equalsIgnoreCase("home")) {
                            vibrator.vibrate(30);
                            intent = (Intent) new Intent(getApplicationContext(), TripTracker.class);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.startActivity(intent);
                            }
                        }, 1000);
                    }});

    }
}
