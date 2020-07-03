package com.example.anb.asynctasks

import android.os.AsyncTask
import android.util.Log
import com.example.anb.asynctasks.FetchCurrent.AsyncResponse
import com.example.anb.Pojo.HistoricalPojo
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object FetchHistorical {
    private const val OPEN_WEATHER_MAP_DAYS_URL = "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=%s&lon=%s&dt=%s&units=%s&appid=58c4a95deb0f81dc05c4cf0788970d99"

    class reportTask(asyncResponse: AsyncResponse?) : AsyncTask<String?, Void?, Array<HistoricalPojo?>>() {
        var delegate: AsyncResponse? = null //Call back interface
        private var result: String? = null
        override fun doInBackground(vararg params: String?): Array<HistoricalPojo?>? {
            val days = params[2]!!.toIntOrNull()
            val units = params[3]
            var i = 0
            val cal = Calendar.getInstance()
            if (days!! > 1) cal.add(Calendar.DATE, -1)
            val jsonWeatherArray = arrayOfNulls<HistoricalPojo>(days)
            while (i < days) {
                try {
                    val currDay = (cal.timeInMillis / 1000L).toString()
                    cal.add(Calendar.DATE, -1)
                    jsonWeatherArray[i] = getWeatherJSON(params[0], params[1], currDay, units)
                    i++
                } catch (e: Exception) {
                    Log.d("Error", "Cannot process JSON results", e)
                }
            }
            return jsonWeatherArray
        }

        override fun onPostExecute(jsonArray: Array<HistoricalPojo?>) {
            val weatherData: MutableList<String?> = object : ArrayList<String?>() {}
            for (historicalPojo in jsonArray) {
                val data = StringBuilder()
                try {
                    if (historicalPojo != null) {
                        val myData = """$historicalPojo""".trimIndent()
                        result = "Success"
                        data.append(myData)
                        weatherData.add(data.toString())
                    } else {
                        val weatherDataString = " - - - - - - "
                        result = "Failure"
                        weatherData.add(weatherDataString)
                    }
                } catch (e: Exception) {
                    //Log.e(LOG_TAG, "Cannot process JSON results", e);
                }
            }
            delegate!!.processFinish((weatherData as ArrayList<String?>), result)
        }

        companion object {
            fun getWeatherJSON(lat: String?, lon: String?, day: String?, unit: String?): HistoricalPojo? {
                val data: JSONObject? = null
                return try {
                    val json = StringBuilder(1024)
                    val url = URL(String.format(OPEN_WEATHER_MAP_DAYS_URL, lat, lon, day, unit))
                    Log.i("url ", url.toString())
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.doInput = true
                    connection.doOutput = true
                    connection.connect()
                    val reader = BufferedReader(
                            InputStreamReader(connection.inputStream))
                    var tmp: String? = ""
                    while (reader.readLine().also { tmp = it } != null) json.append(tmp).append("\n")
                    reader.close()
                    connection.disconnect()
                    val gson = Gson()
                    val historicalPojo: HistoricalPojo
                    historicalPojo = gson.fromJson(json.toString(), HistoricalPojo::class.java)
                    historicalPojo
                } catch (e: Exception) {
                    null
                }
            }
        }

        init {
            delegate = asyncResponse //Assigning call back interface through constructor
        }
    }
}