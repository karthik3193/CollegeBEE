package com.karthik.collegebee;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripTracker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference dloc;
    Loc loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_tracker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        readData();
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
    }
    public void readData()
    {
        dloc = FirebaseDatabase.getInstance().getReference("Loc");
// Read from the database
        dloc.addValueEventListener(new

                                           ValueEventListener() {
                                               @Override
                                               public void onDataChange (DataSnapshot dataSnapshot){
                                                   // This method is called once with the initial value and again
                                                   // whenever data at this location is updated.
                                                   loc = dataSnapshot.getValue(Loc.class);
                                                   Toast.makeText(getApplicationContext(),"CONNECTED,wait..",Toast.LENGTH_LONG).show();
                                                   addmarker();
                                               }

                                               @Override
                                               public void onCancelled (DatabaseError error){
                                                   // Failed to read value
                                               }
                                           });
    }
    public void addmarker(){
        LatLng Myloc = new LatLng(loc.lat, loc.lng);
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus)).position(Myloc).title("Tracked BUS").visible(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Myloc, 25));
    }
}
