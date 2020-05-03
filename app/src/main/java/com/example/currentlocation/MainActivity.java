package com.example.currentlocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView start, stop;
    TextView distance;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Double latitude, longitude,s_latitude,s_longitude;
    Geocoder geocoder;
    List<Address> addresses;

    int count=0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start_id);
        stop = findViewById(R.id.stop_id);
        distance = findViewById(R.id.distance_id);
        geocoder = new Geocoder(this, Locale.ENGLISH);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
       // currentlocation(location);
    }

    @Override
    public void onClick(View view) {
        if(view==start)
        {
            //startService(new Intent(this,ServiceClass.class));
             locationCallback=new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        latitude= location.getLatitude();
                        longitude=location.getLongitude();
                        if(count==0)
                        {
                            s_longitude=location.getLongitude();
                            s_latitude =location.getLatitude();
                        }
                        count++;


                        try {
                            addresses= geocoder.getFromLocation(latitude,longitude,1);
                            Address address=addresses.get(0);
                            String loca=address.getAddressLine(0)+"\t"+address.getPostalCode()+"\t"+address.getLocality()+"\t"+address.getCountryCode();
                            Toast.makeText(getApplicationContext(),loca,Toast.LENGTH_LONG).show();
                            try{

                                float result[]=new float[10];
                                Location.distanceBetween(s_latitude,s_longitude,latitude,longitude,result);

                                distance.setText("Distance = "+result[0]/1000+" Km");

                            }catch (Exception e)
                            {
                                Toast.makeText(MainActivity.this," "+e,Toast.LENGTH_LONG).show();
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(MainActivity.this,s_latitude+" "+s_longitude,Toast.LENGTH_LONG).show();
                    }

                }
            };
            oncreateloaction();
           /*Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("lati",latitude);
            intent.putExtra("long",longitude);
            startActivity(intent);*/

        }
        else if(view==stop)
        {

           // stopService(new Intent(this,ServiceClass.class));
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
           // onPause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(fusedLocationProviderClient!=null)
        {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    private void oncreateloaction() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
