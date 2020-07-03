package com.example.anb.ui.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.anb.asynctasks.FetchCurrent.AsyncResponse
import com.example.anb.asynctasks.FetchCurrent.mainscreenTask
import com.example.anb.R

/**
 * A placeholder fragment containing a simple view.
 */
class CurrentFragment : Fragment() {
    var cityField: TextView? = null
    var detailsField: TextView? = null
    var currentTemperatureField: TextView? = null
    var humidity_field: TextView? = null
    var pressure_field: TextView? = null
    var weatherIcon: TextView? = null
    var updatedField: TextView? = null
    var userName: TextView? = null
    private var progressBar: ProgressBar? = null
    private var asyncTask: mainscreenTask? = null
    var units = "metric"
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val b = sharedPreferences.getBoolean("tempUnit", true)
            field = if (b) "imperial" else "metric"
            return field
        }
        private set

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_current, container, false)
        cityField = root.findViewById<View>(R.id.city_field) as TextView
        updatedField = root.findViewById<View>(R.id.updated_field) as TextView
        detailsField = root.findViewById<View>(R.id.details_field) as TextView
        currentTemperatureField = root.findViewById<View>(R.id.current_temperature_field) as TextView
        humidity_field = root.findViewById<View>(R.id.humidity_field) as TextView
        pressure_field = root.findViewById<View>(R.id.pressure_field) as TextView
        weatherIcon = root.findViewById<View>(R.id.weather_icon) as TextView
        progressBar = root.findViewById(R.id.progressBar)
        userName = root.findViewById(R.id.user_name)
        userName!!.text = username
        asyncTask = mainscreenTask(object : AsyncResponse {
            override fun processFinish(data: ArrayList<String?>, output: String?) {
                progressBar!!.visibility = View.INVISIBLE
                if (!output.equals("failure", ignoreCase = true)) {
                    val weatherDayData = data[0]?.split("-".toRegex())?.toTypedArray()
                    val city = weatherDayData?.get(0)
                    val description = weatherDayData?.get(1)
                    val temp = weatherDayData?.get(2)
                    val humidity = weatherDayData?.get(3)
                    val pressure = weatherDayData?.get(4)
                    //     String updatedOn = weatherDayData[5];
                    cityField!!.text = city//weather_city
                    //   updatedField.setText(weatherDayData[5]);
                    detailsField!!.text = description
                    currentTemperatureField!!.text = temp
                    humidity_field!!.text = "Humidity: " + humidity
                    pressure_field!!.text = "Pressure: " + pressure
                } else Toast.makeText(context, output, Toast.LENGTH_SHORT).show()
            }
        })
        val sharedPref = activity!!.getSharedPreferences("latlng", Context.MODE_PRIVATE)
        if (sharedPref.contains("lat+long")) {
            val latlong = sharedPref.getString("lat+long", null)!!.split("-".toRegex()).toTypedArray()
            Log.i("lat", latlong[0])
            asyncTask!!.execute(latlong[0], latlong[1], units)
        } else {
            Toast.makeText(context, "Please go back and start the app again !", Toast.LENGTH_LONG).show()
        }
        return root
    }

    val username: String?
        get() {
            var name = " "
            val sharedPref = activity!!.getSharedPreferences("username", Context.MODE_PRIVATE)
            if (sharedPref.contains("username")) {
                name = sharedPref.getString("username", null).toString()
            }
            return name
        }

    companion object {
        val instance: CurrentFragment
            get() = CurrentFragment()
    }
}