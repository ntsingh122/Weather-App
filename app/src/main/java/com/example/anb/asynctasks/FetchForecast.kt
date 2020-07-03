package com.example.anb.asynctasks

import android.os.AsyncTask
import android.util.Log
import com.example.anb.asynctasks.FetchCurrent.AsyncResponse
import com.example.anb.Pojo.ForecastPojo
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object FetchForecast {
    private const val OPEN_WEATHER_MAP_DAYS_URL = "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=%s&appid=58c4a95deb0f81dc05c4cf0788970d99"

    class forecastTask(asyncResponse: AsyncResponse?) : AsyncTask<String?, Void?, Array<ForecastPojo?>>() {
        var delegate: AsyncResponse? = null //Call back interface
        private var result: String? = null
        override fun doInBackground(vararg params: String?): Array<ForecastPojo?>? {
            val days = 1
            val units = params[2]
            var i = 0
            val jsonWeatherArray = arrayOfNulls<ForecastPojo>(days)
            while (i < days) {
                try {
                    jsonWeatherArray[i] = getWeatherJSON(params[0], params[1], units)
                    i++
                } catch (e: Exception) {
                    Log.d("Error", "Cannot process JSON results", e)
                }
            }
            return jsonWeatherArray
        }

        override fun onPostExecute(jsonArray: Array<ForecastPojo?>) {
            val weatherData: MutableList<String?> = object : ArrayList<String?>() {}
            for (forecastPojo in jsonArray) {
                val listSize = forecastPojo!!.list.size
                try {
                    if (forecastPojo != null) {
                        var i = 1
                        while (i < listSize) {
                            val weather = """

                                Weather : ${forecastPojo.list[i].weather[0].description}
                                """.trimIndent()
                            val temp = """

                                Temprature  : ${forecastPojo.list[i].main.temp}
                                """.trimIndent()
                            val temp_min = """

                                Temprature Min : ${forecastPojo.list[i].main.temp_min}
                                """.trimIndent()
                            val temp_max = """

                                Temprature Max : ${forecastPojo.list[i].main.temp_max}
                                """.trimIndent()
                            val humid = """

                                Humidity : ${forecastPojo.list[i].main.humidity}
                                """.trimIndent()
                            val pressure = """

                                Pressure : ${forecastPojo.list[i].main.pressure}
                                """.trimIndent()
                            val feel_like = """

                                Feels Like ${forecastPojo.list[i].main.feels_like}
                                """.trimIndent()
                            val speed = """

                                Speed : ${forecastPojo.list[i].wind.speed}
                                """.trimIndent()
                            val degree = """

                                Degree : ${forecastPojo.list[i].wind.deg}
                                """.trimIndent()
                            val date = """

                                Date : ${forecastPojo.list[i].dtTxt}
                                """.trimIndent()
                            val city ="""
                                
                                
                                City Name : ${forecastPojo.city?.name}
                                """.trimIndent()
                            val allData = """
                                $date$city$weather$temp$temp_max$temp_min$humid$pressure$feel_like$speed$degree

                                """.trimIndent()
                            weatherData.add(allData)
                            //this line here skips data of more than one time on  a single day  to get all data remove i=i+6
                            // and also go to getDtTxt funcn and remove substring part to show time
                            i = i + 7
                            i++
                        }
                        result = "Success"
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
            fun getWeatherJSON(lat: String?, lon: String?, unit: String?): ForecastPojo? {
                val data: JSONObject? = null
                return try {
                    val json = StringBuilder(1024)
                    val url = URL(String.format(OPEN_WEATHER_MAP_DAYS_URL, lat, lon, unit))
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
                    val forecastPojo: ForecastPojo
                    forecastPojo = gson.fromJson(json.toString(), ForecastPojo::class.java)
                    forecastPojo
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