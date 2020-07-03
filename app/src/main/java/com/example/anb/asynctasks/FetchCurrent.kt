package com.example.anb.asynctasks

import android.os.AsyncTask
import android.util.Log
import com.example.anb.Pojo.CurrentDayPojo
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.util.*

object FetchCurrent {
    private const val OPEN_WEATHER_MAP_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=%s&appid=58c4a95deb0f81dc05c4cf0788970d99"
    fun getWeatherMainJSON(lat: String?, lon: String?, unit: String?): CurrentDayPojo? {
        val data: JSONObject? = null
        return try {
            val json = StringBuilder(1024)
            val url = URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon, unit))
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
            val currentDayPojo: CurrentDayPojo
            currentDayPojo = gson.fromJson(json.toString(), CurrentDayPojo::class.java)

//
            currentDayPojo
        } catch (e: Exception) {
            null
        }
    }

    interface AsyncResponse {
        fun processFinish(data: ArrayList<String?>, output: String?)
    }

    class mainscreenTask(asyncResponse: AsyncResponse?) : AsyncTask<String?, Void?, CurrentDayPojo?>() {
        var delegate: AsyncResponse? = null //Call back interface
        private var result: String? = null
//        protected override fun doInBackground(vararg params: String): CurrentDayPojo? {
//            val unit = params[2]
//            return getWeatherMainJSON(params[0], params[1], unit)
//        }

        override fun onPostExecute(currentDayPojo: CurrentDayPojo?) {
            val weatherData: ArrayList<String?> = object : ArrayList<String?>() {}
            var weatherDataString: String
            val data = StringBuilder()
            try {
//                        JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                if (currentDayPojo != null) {
                    val weather = currentDayPojo.weather[0]
                    //                        JSONObject main = myPojo.getJSONObject("main");
                    val main = weather.main
                    val df = DateFormat.getDateTimeInstance()

//                        String city = myPojo.getString("name").toUpperCase(Locale.US) + ", " + myPojo.getJSONObject("sys").getString("country");
                    val city = currentDayPojo.name
                    val description = weather.description.toUpperCase(Locale.US)
                    //                        String description = weather.getString("description").toUpperCase(Locale.US);
                    val temperature = currentDayPojo.main.temp + "°"
                    //                        String temperature = String.format("%.2f", main.getDouble("temp")) + "°";
                    val humidity = currentDayPojo.main.humidity + "%"
                    //                        String humidity = main.getString("humidity") + "%";
                    val pressure = currentDayPojo.main.pressure + " hPa"
                    //  String updatedOn = df.format(new Date(currentDayPojo.dt * 1000));
                    result = "Success"
                    data.append("$city-$description-$temperature-$humidity-$pressure-")
                    weatherData.add(data.toString())
                } else {
                    weatherDataString = " - - - - - - "
                    result = "Failure"
                    weatherData.add(weatherDataString)
                }
            } catch (e: Exception) {
                //Log.e(LOG_TAG, "Cannot process JSON results", e);
            }
            delegate!!.processFinish(weatherData, result)
        }

        init {
            delegate = asyncResponse //Assigning call back interface through constructor
        }

        override fun doInBackground(vararg params: String?): CurrentDayPojo? {
            val unit = params[2]
            return getWeatherMainJSON(params[0], params[1], unit)
        }
    }
}