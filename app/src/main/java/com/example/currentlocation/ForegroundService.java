package com.example.currentlocation;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ForegroundService extends Service {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static final String DEBUG_TAG = "TAG";
    public Activity activity;

    int count=0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Double latitude, longitude,s_latitude,s_longitude;
    Geocoder geocoder;
    List<Address> addresses;
    String distanceInKm;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        startForeground();

        return super.onStartCommand(intent, flags, startId);

    }



    private void startForeground() {

        geocoder = new Geocoder(this, Locale.ENGLISH);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                        //Toast.makeText(getApplicationContext(),loca,Toast.LENGTH_LONG).show();
                        MainActivity.address.setText(loca);
                        try{

                            float result[]=new float[10];
                            Location.distanceBetween(s_latitude,s_longitude,latitude,longitude,result);

                            MainActivity.distance.setText("Distance = "+result[0]/1000+" Km");
                            distanceInKm = String.valueOf(result[0]/1000)+ " Km";
                            notification(distanceInKm);

                        }catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext()," "+e,Toast.LENGTH_LONG).show();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        oncreateloaction();

    }

    private void notification(String distance) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(distance)
                .setContentIntent(pendingIntent)
                .build());

    }

    private void oncreateloaction() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


}