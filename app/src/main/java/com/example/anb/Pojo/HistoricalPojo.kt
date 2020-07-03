package com.example.anb.Pojo

import java.text.DateFormat
import java.util.*

class HistoricalPojo {

    var current: Current? = null
    var timezone: String? = null
    var timezone_offset: String? = null
    var lon: String? = null
    var lat: String? = null

    override fun toString(): String {
        return """${current.toString()}
 Timezone = $timezone
 Longitude = $lon
 Latitude = $lat"""
    }

    inner class Current {
        var sunrise: String? = null
        var temp: String? = null
        var visibility: String? = null
        var uvi: String? = null
        var pressure: String? = null
        var clouds: String? = null
        var feels_like: String? = null
        var dt: Long? = null
        var wind_deg: String? = null
        var dew_point: String? = null
        var sunset: String? = null
        lateinit var weather: Array<Weather>
        var humidity: String? = null
        var wind_speed: String? = null

        override fun toString(): String {
            return """ 
 Date = ${getTime(dt)}
 Temperature = $temp°
 Visibility = $visibility
 Uvi = $uvi
 Pressure = $pressure hPa
 Clouds = $clouds
 Feels Like = $feels_like°
 Wind degree = $wind_deg
 Dew Point = $dew_point
${weather[0]}
 Humidity = $humidity %
 Wind Speed = $wind_speed"""
        }

        //Sunrise = ${getTime(sunrise)}
// Sunset = ${getTime(sunset)}
        private fun getTime(date: String?): String {
            val dt = date!!.toLong()
            val df = DateFormat.getDateTimeInstance()
            return df.format(Date(dt * 1000))
        }

        private fun getTime(date: Long?): String {
            val df = DateFormat.getDateTimeInstance()
            return df.format(Date(date!! * 1000))
        }
    }

    inner class Weather {
        var icon: String? = null
        var description: String? = null
        var main: String? = null
        var id: String? = null

        override fun toString(): String {
            return " Description = $description"
        }
    }
}