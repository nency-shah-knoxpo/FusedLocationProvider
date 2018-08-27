package com.example.applecustomer.fusedlocationprovider;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

public class LocationService extends IntentService

{
    public static final String ACTION_PROCESS_UPDATES = "123";

    public LocationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                       String longitude = String.valueOf(location.getLongitude());
                       String latitude = String.valueOf(location.getLatitude());

                    SharedPreferences.Editor editor = getSharedPreferences("location",MODE_PRIVATE).edit();
                    editor.putString("lat", longitude);
                    editor.putString("lon",latitude);
                    editor.apply();
                }
            }
        }
    }

}

