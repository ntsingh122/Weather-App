package com.example.anb.ui.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.anb.asynctasks.FetchCurrent.AsyncResponse
import com.example.anb.asynctasks.FetchForecast.forecastTask
import com.example.anb.asynctasks.FetchHistorical.reportTask
import com.example.anb.R
import java.util.*

class Report : Fragment() {
    private var reportAsyncTask: reportTask? = null
    private var forecastAsyncTask: forecastTask? = null
    private val dataText: TextView? = null
    private var listView: ListView? = null
    var units: String? = null
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val b = sharedPreferences.getBoolean("tempUnit", true)
            field = if (b) "imperial" else "metric"
            return field
        }
        private set
    private var progressBar: ProgressBar? = null
    private val choicesArray = arrayOf<String?>("Historical Data", "Forecast Data")
    private var spinner: Spinner? = null
    private var submit: Button? = null

    //Change here for no of days dont put it more than 5
    private val FINAL_NO_DAYS = "5"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_report, container, false)
        listView = root.findViewById(R.id.listView)
        progressBar = root.findViewById(R.id.progressBarReport)
        progressBar!!.visibility = View.INVISIBLE
        spinner = root.findViewById<View>(R.id.spinnerReport) as Spinner
        submit = root.findViewById(R.id.spinnerReportSubmitButton)
        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_item, choicesArray)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        spinner!!.adapter = aa



        submit!!.setOnClickListener {
            val type = spinner!!.selectedItem as String
            progressBar!!.visibility = View.VISIBLE
            val sharedPref = activity!!.getSharedPreferences("latlng", Context.MODE_PRIVATE)
            if (sharedPref.contains("lat+long")) {
                val latlong = sharedPref.getString("lat+long", null)!!.split("-".toRegex()).toTypedArray()
                if (type.equals("Historical Data", ignoreCase = true)) createReportAsyncTask().execute(latlong[0], latlong[1], "3", units)
                if (type.equals("Forecast Data", ignoreCase = true)) createForecastAsyncTask().execute(latlong[0], latlong[1], units)
            }
            submit!!.isEnabled = false
        }
        return root
    }

    private fun createForecastAsyncTask(): forecastTask {
        if (forecastAsyncTask == null) {
            return forecastTask(
                    object : AsyncResponse {
                        override fun processFinish(data: ArrayList<String?>, output: String?) {
                            onTaskFinishData(data, output)
                        }
                    }).also { forecastAsyncTask = it }
        }
        forecastAsyncTask!!.cancel(true)
        return forecastTask(object : AsyncResponse {
            override fun processFinish(data: ArrayList<String?>, output: String?) {
                onTaskFinishData(data, output)
            }
        }).also { forecastAsyncTask = it }
    }

    private fun createReportAsyncTask(): reportTask {
        if (reportAsyncTask == null) {
            return reportTask(object : AsyncResponse {
                override fun processFinish(data: ArrayList<String?>, output: String?) {
                    onTaskFinishData(data, output)
                }
            }).also { reportAsyncTask = it }
        }
        reportAsyncTask!!.cancel(true)
        return reportTask(object : AsyncResponse {
            override fun processFinish(data: ArrayList<String?>, output: String?) {
                onTaskFinishData(data, output)
            }
        }).also { reportAsyncTask = it }
    }

    private fun onTaskFinishData(weatherData: ArrayList<String?>, result: String?) {
        progressBar!!.visibility = View.INVISIBLE
        if (!result.equals("failure", ignoreCase = true)) {
            progressBar!!.visibility = View.INVISIBLE
            val adapter = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, weatherData) }
            listView!!.adapter = adapter
        } else Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        submit!!.isEnabled = true
    }


    companion object {
        fun newInstance(): Report {
            return Report()
        }
    }
}