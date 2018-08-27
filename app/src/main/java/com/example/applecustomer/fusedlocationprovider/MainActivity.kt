package com.example.applecustomer.fusedlocationprovider

import android.annotation.TargetApi
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes

import java.util.ArrayList

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    var txtOutputLat: TextView? = null
    var txtOutputLon: TextView? = null
    var mLastLocation: Location? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var lat: String? = null
    var lon: String? = null

    val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1


    private/* Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/ val pendingIntent: PendingIntent
        get() {

            val intent = Intent(this, LocationUpdateBroadcastReceiver::class.java)
            intent.action = LocationUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buildGoogleApiClient()

        val sp = getSharedPreferences("location", Context.MODE_PRIVATE)
        val latitude = sp.getString("lat", "")
        val longitude = sp.getString("lon", "")

        Toast.makeText(this, "$longitude   $latitude", Toast.LENGTH_LONG).show()
        this.setFinishOnTouchOutside(true)

        txtOutputLat = findViewById(R.id.latTV)
        txtOutputLon = findViewById(R.id.lngTV)

        val btn = findViewById<Button>(R.id.removeLocBtn)
        btn.setOnClickListener { view -> removeLocationUpdates(view) }


        /*  Intent bindIntent = new Intent(this,LocationService.class);
        startService(bindIntent);*/

        //bindService(bindIntent,mConnection,0);
        /*final LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        if(hasGPSDevice(MainActivity.this)){
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLoc();
            } else {
                Toast.makeText(MainActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            }

        }
        else{

        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }

        if (!hasGPSDevice(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            Log.e("keshav", "Gps already enabled");
            Toast.makeText(MainActivity.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
            Log.e("keshav", "Gps already enabled");
            Toast.makeText(MainActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }*/

    }

    fun removeLocationUpdates(view: View) {
        val intent = Intent(this, LocationUpdateBroadcastReceiver::class.java)
        intent.action = LocationUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel()
    }


    private fun enableLoc() {

        if (mGoogleApiClient != null) {


            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = (30 * 1000).toLong()
            locationRequest.fastestInterval = (5 * 1000).toLong()
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)

            builder.setAlwaysShow(true)

            val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
            result.setResultCallback { result ->
                val status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(this@MainActivity, REQUEST_LOCATION)

                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            Log.d("xyz", "press ok")
            updateUI()
        } else {

            //user don't allow to enable the location
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val mLocationRequest = LocationRequest.create()
                    mLocationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
                    mLocationRequest.interval = 10000 // Update location every second

                    if (checkPermission()) {

                        enableLoc()

                        /* final LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
                            enableLoc();
                        }
*/
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingIntent)


                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient)
                        if (mLastLocation != null) {
                            lat = mLastLocation!!.latitude.toString()
                            lon = mLastLocation!!.longitude.toString()

                        }
                        updateUI()
                    }

                } else {

                }
            }
        }
    }


    override fun onConnected(bundle: Bundle?) {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 1000

        if (checkPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingIntent)

            enableLoc()
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient)
            if (mLastLocation != null) {
                lat = mLastLocation!!.latitude.toString()
                lon = mLastLocation!!.longitude.toString()

            }
            updateUI()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }

    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location) {
        Log.d("x", "after press ok")
        lat = location.latitude.toString()
        lon = location.longitude.toString()
        updateUI()
    }


    @Synchronized
    internal fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    internal fun updateUI() {
        txtOutputLat!!.text = lat
        txtOutputLon!!.text = lon
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient!!.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        mGoogleApiClient!!.disconnect()
    }

    override fun onStop() {
        super.onStop()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun hasGPSDevice(context: Context): Boolean {
        val mgr = context
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager ?: return false
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    companion object {
        internal val REQUEST_LOCATION = 199
    }
}
