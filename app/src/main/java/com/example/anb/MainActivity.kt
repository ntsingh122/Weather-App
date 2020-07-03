package com.example.anb

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/*TODO add 4 tabs related to it
        tab 1 :current current weather with location and username
        tab 2 :create a datePicker and spinner to select city show city  on mapFragment
        tab 3: 30 day weather report can be history or forecast
        tab 4: settings to select f or c default c
        weather report - city name Temp
        wind speed etc...
     */
class MainActivity : AppCompatActivity() {
    private var permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private var button: Button? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var editText: EditText? = null
    private var mLocationManager: LocationManager? = null
    private val myRequestCode = 1

    //Typeface weatherFont;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        editText = findViewById(R.id.editTextTextPersonName)
        button!!.isEnabled = false
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

//        mLocationManager.requestLocationUpdatesdates(LocationManager.GPS_PROVIDER, 5000, 10, (LocationListener) this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, myRequestCode)
        } else {
            fetchGps()
        }
        button!!.isEnabled = true
        button!!.setOnClickListener {
            if (editText!!.text != null) {
                val sharedPref = getSharedPreferences("username", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("username", editText!!.text.toString())
                editor.apply()
                startActivity(Intent(this@MainActivity, DisplayActivity::class.java))
            } else Toast.makeText(this@MainActivity, "Please Input username first", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                1 -> fetchGps()
                else -> {
                }
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchGps() {
        fusedLocationClient!!.lastLocation
                .addOnSuccessListener(this) { location ->
                    //Toast.makeText(this@MainActivity, "location success", Toast.LENGTH_LONG).show()
                    if (location != null) {
                        val sharedPref = getSharedPreferences("latlng", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("lat+long", location.latitude.toString() + "-" + location.longitude)
                        editor.apply()
                    } else Toast.makeText(this@MainActivity, "Please Provide Location First !", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { Toast.makeText(this@MainActivity, "failed", Toast.LENGTH_LONG).show() }
    }
}