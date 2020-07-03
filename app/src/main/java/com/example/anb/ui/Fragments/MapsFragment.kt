package com.example.anb.ui.Fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.anb.asynctasks.FetchCurrent.AsyncResponse
import com.example.anb.asynctasks.FetchForecast.forecastTask
import com.example.anb.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var button: Button? = null
    private var spin: Spinner? = null
    private var textView: TextView? = null
    private var asyncTask: forecastTask? = null
    var units = "metric"
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val b = sharedPreferences.getBoolean("tempUnit", true)
            field = if (b) "imperial" else "metric"
            return field
        }
        private set
    var currentDate = LocalDate.now().toString()
    private var dateFrom = currentDate
    private var difference = 0
    var date = currentDate
    private var progressBar: ProgressBar? = null
    var country = arrayOf<String?>("Mumbai", "Delhi", "Noida")
    var latLngs = arrayOf(LatLng(19.076090, 72.877426), LatLng(28.7041, 77.1025), LatLng(28.5355, 77.3910))
    private var mDateSetListener1: OnDateSetListener? = null
    private var mDisplayDateFrom: TextView? = null
    private var currentLocalDate: LocalDate? = null
    private var dateAfter: LocalDate? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_maps, container, false)
        mapFragment = this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        spin = root.findViewById<View>(R.id.spinner) as Spinner
        button = root.findViewById(R.id.findButton)
        textView = root.findViewById(R.id.textViewMaps)
        progressBar = root.findViewById(R.id.progressBarMaps)
        progressBar!!.visibility = View.INVISIBLE
        mDisplayDateFrom = root.findViewById(R.id.date_picker)
        mDisplayDateFrom!!.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
            val day = cal[Calendar.DAY_OF_MONTH]
            val dialog = DatePickerDialog(
                    context!!,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener1,
                    year, month, day)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        })
        mDateSetListener1 = OnDateSetListener { datePicker, year, month, day ->
            var month = month
            month = month + 1
            dateFrom = year.toString() + "-" + String.format("%02d", month) + "-" + String.format("%02d", day)
            mDisplayDateFrom!!.text = dateFrom
        }


        //Creating the ArrayAdapter instance having the country list
        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_item, country)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        spin!!.adapter = aa
        button!!.setOnClickListener {
            val city = spin!!.selectedItem as String
            Toast.makeText(context, city, Toast.LENGTH_SHORT).show()
            val cityId = spin!!.selectedItemPosition
            if (checkDates()) {
                updateMap(latLngs[cityId])
                progressBar!!.visibility = View.VISIBLE
                createAsyncTask().execute(latLngs[cityId].latitude.toString(), latLngs[cityId].longitude.toString(), units)
                button!!.isEnabled = false
            }
        }
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    private fun updateMap(currentLoc: LatLng) {
        mGoogleMap!!.clear()
        mGoogleMap!!.addMarker(MarkerOptions().position(currentLoc).title("Current Location"))
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 10f))
    }

    private val currentLocationLatlong: Array<String>
        private get() {
            val sharedPref = activity!!.getSharedPreferences("latlng", Context.MODE_PRIVATE)
            return sharedPref.getString("lat+long", null)!!.split("-".toRegex()).toTypedArray()
        }

    private fun createAsyncTask(): forecastTask {
        if (asyncTask == null) {
            return forecastTask(
                    object : AsyncResponse {
                        override fun processFinish(data: ArrayList<String?>, output: String?) {
                            onTaskFinishData(data, output)
                        }
                    }).also { asyncTask = it }
        }
        asyncTask!!.cancel(true)
        return forecastTask(object : AsyncResponse {
            override fun processFinish(data: ArrayList<String?>, output: String?) {
                onTaskFinishData(data, output)
            }
        }).also { asyncTask = it }
    }

    private fun onTaskFinishData(weatherData: ArrayList<String?>, result: String?) {
        var index = 0
        index = index + 7 * difference
        progressBar!!.visibility = View.INVISIBLE
        if (!result.equals("failure", ignoreCase = true)) {
            if (difference > weatherData.size) {
                Toast.makeText(context, "Date Exceeding data", Toast.LENGTH_SHORT).show()
            } else {
                val weatherDayData = weatherData[difference]
                textView!!.text = weatherDayData
            }
        } else Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        button!!.isEnabled = true
    }

    private fun checkDates(): Boolean {
        val cDate = currentDate
        val sDate = dateFrom //.substring(0,date.length()-1);;
        try {
            currentLocalDate = LocalDate.parse(cDate)
            dateAfter = LocalDate.parse(sDate)
            return if (dateAfter!!.isBefore(currentLocalDate) || dateAfter!!.isAfter(currentLocalDate!!.plusDays(4))) {
                Toast.makeText(context, "Please select date within next 5 days!", Toast.LENGTH_SHORT).show()
                false
            } else {
                val diff = ChronoUnit.DAYS.between(currentLocalDate, dateAfter)
                difference = diff.toInt()
                true
            }
        } catch (pe: Exception) {
            pe.printStackTrace()
            Log.i("error :", pe.toString())
        }
        return false
    }

    companion object {
        val instance: MapsFragment
            get() = MapsFragment()
    }
}