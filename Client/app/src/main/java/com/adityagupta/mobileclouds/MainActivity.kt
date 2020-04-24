package com.adityagupta.mobileclouds

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.adityagupta.mobileclouds.utils.NetworksHelper
import com.adityagupta.mobileclouds.utils.PrefsHelper
import com.adityagupta.mobileclouds.utils.RetrofitHelper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var permissionID = 44
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textV.text = "Welcome ${PrefsHelper().getName(applicationContext)}"
        nameField.setText(PrefsHelper().getServerIP(applicationContext))
        buttonLogin.setOnClickListener {
            val name = (findViewById<View>(R.id.nameField) as EditText).text.toString().trim { it <= ' ' }
            PrefsHelper().setServerIP(applicationContext, name.toLowerCase(Locale.getDefault()).replace(" ", "-"))
            refreshToken()
        }
        myJobsButton.setOnClickListener {
            startActivity(Intent(this, MyJobsActivity::class.java))
        }
        savedJobs.setOnClickListener {
            startActivity(Intent(this, SavedJobsActivity::class.java))
        }
        refreshToken()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ipAddress.text = "Your IP: ${NetworksHelper.getIpAddress()}"

        getLastLocation()
        setupNetworkConnectionListener()
    }

    private fun setupNetworkConnectionListener() {
        val conManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        conManager.registerNetworkCallback(NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                RetrofitHelper.updateTokenOnServer(this@MainActivity,
                    PrefsHelper().getName(applicationContext),
                    NetworksHelper.getIpAddress(),
                    PrefsHelper().getLat(applicationContext),
                    PrefsHelper().getLong(applicationContext),
                    PrefsHelper().getToken(applicationContext)
                )
            }

            override fun onLost(network: Network) {
            }
        }
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        updateLongLat(location)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            permissionID
        )
    }

    private fun updateLongLat(location: Location) {
        Log.e(TAG, "lat: ${location.latitude}, long: ${location.longitude}")
        PrefsHelper().setLat(applicationContext, location.latitude.toString())
        PrefsHelper().setLong(applicationContext, location.longitude.toString())

        RetrofitHelper.updateTokenOnServer(this@MainActivity,
            PrefsHelper().getName(applicationContext),
            NetworksHelper.getIpAddress(),
            PrefsHelper().getLat(applicationContext),
            PrefsHelper().getLong(applicationContext),
            PrefsHelper().getToken(applicationContext)
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location = locationResult.lastLocation
            updateLongLat(location)
        }
    }

    private fun refreshToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                Log.e(TAG, "onComplete: " + task.result!!.token)
                PrefsHelper().setToken(applicationContext, task.result!!.token)
                RetrofitHelper.updateTokenOnServer(this@MainActivity,
                    PrefsHelper().getName(applicationContext),
                    NetworksHelper.getIpAddress(),
                    PrefsHelper().getLat(applicationContext),
                    PrefsHelper().getLong(applicationContext),
                    PrefsHelper().getToken(applicationContext)
                )
            })
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}