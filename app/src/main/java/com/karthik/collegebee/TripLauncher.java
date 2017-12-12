package com.karthik.collegebee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.kjulio.rxlocation.RxLocation;

import io.reactivex.functions.Consumer;

public class TripLauncher extends AppCompatActivity {
    Button btn;
    ProgressDialog progressDialog;
    DatabaseReference dloc;
    LocationRequest locationRequest;
    double lat=0,lng=0;
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triplauncher);
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "Requires a INTERNET Connection!!!", Toast.LENGTH_SHORT).show();
            finish();
        }
        dloc= FirebaseDatabase.getInstance().getReference("Loc");
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
        btn=(Button) findViewById(R.id.lun);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProgress();
                   LocateMe();
                   addDetails();


            }
        });
    }
    public int LocateMe(){
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
        return 1;

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
            progressDialog.setMessage("Tracking Started!!!");
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    private void addDetails(){
        Loc loc=new Loc(lat,lng);
        dloc.setValue(loc);



    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
