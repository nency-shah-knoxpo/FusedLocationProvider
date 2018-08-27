package com.example.applecustomer.fusedlocationprovider

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.android.gms.location.LocationResult

import android.content.Context.MODE_PRIVATE
import com.example.applecustomer.fusedlocationprovider.LocationService.Companion.ACTION_PROCESS_UPDATES

class LocationUpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("reciver", "broadcast recivee")
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {
                /*val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val location = result.lastLocation
                    val longitude = location.longitude.toString()
                    val latitude = location.latitude.toString()

                    val editor = context.getSharedPreferences("location", MODE_PRIVATE).edit()
                    editor.putString("lat", latitude)
                    editor.putString("lon", longitude)
                    showNotification(context, longitude, latitude)
                    editor.apply()*/


                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val location = result.lastLocation
                    val longitude = location.longitude.toString()
                    val latitude = location.latitude.toString()

                  showNotification(context,longitude,latitude)

                    var i = Intent(context, LocationService::class.java)
                    i.action = LocationService.ACTION_PROCESS_UPDATES
                    i.putExtra("lat",latitude)
                    i.putExtra("lon",longitude)
                    context.startService(i)

                }

            }

        }

    }

    fun showNotification(context: Context, longitutde: String, latitude: String) {
        Log.d("notification", "In notification")

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context)
                .setContentTitle("Locations")
                .setContentText("longitude = " + longitutde + "latitude = " + latitude)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, notificationBuilder.build())
    }

    companion object {
        val ACTION_PROCESS_UPDATES = "123"
    }
}


