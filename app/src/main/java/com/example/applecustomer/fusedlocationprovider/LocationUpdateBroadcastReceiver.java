package com.example.applecustomer.fusedlocationprovider;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LocationUpdateBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATES = "123";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("reciver","broadcast recivee");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
              /*  LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    String longitude = String.valueOf(location.getLongitude());
                    String latitude = String.valueOf(location.getLatitude());

                    SharedPreferences.Editor editor = context.getSharedPreferences("location",MODE_PRIVATE).edit();
                    editor.putString("lat", latitude);
                    editor.putString("lon",longitude);
                    editor.apply();*/

                Intent i = new Intent(context, LocationService.class);
                i.setAction(LocationService.ACTION_PROCESS_UPDATES);
                context.startService(i);

                }
            }
        }
    }

