package com.example.applecustomer.fusedlocationprovider

import android.app.IntentService
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.android.gms.location.LocationResult

class LocationService(name: String) : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            Log.d("Service", "In service")

            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {


                    val longitude = intent.getStringExtra("lat")
                    val latitude =intent.getStringExtra("lon")

                    val editor = getSharedPreferences("location", Context.MODE_PRIVATE).edit()
                    editor.putString("lat", longitude)
                    editor.putString("lon", latitude)
                    editor.apply()
                    //   showNotification(longitude,latitude);
                }

        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATES = "123"
    }


}

