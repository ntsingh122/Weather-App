package com.example.anb.Pojo

import kotlin.properties.Delegates

class CurrentDayPojo {

    var dt: Long by Delegates.notNull<Long>()
    lateinit var visibility: String
    lateinit var timezone: String
    lateinit var weather: Array<Weather>
    lateinit var name: String
    lateinit var cod: String
    lateinit var main: Main
    lateinit var id: String

    override fun toString(): String {
        return """Visibility : $visibility
 Timezone : $timezone
 Weather : $weather
 Name : $name$main
 Id : $id"""
    }

    class Main {
        lateinit var temp: String
        lateinit var temp_min: String
        lateinit var humidity: String
        lateinit var pressure: String
        lateinit var feels_like: String
        lateinit var temp_max: String

        override fun toString(): String {
            return " Temp : $temp\n Temp min : $temp_min\n Humidity : $humidity\n Pressure : $pressure\n Feels like : $feels_like\n Temp max : $temp_max"
        }
    }

    class Weather {
        lateinit var main: String
        lateinit var icon: String
        lateinit var description: String
        lateinit var id: String

        override fun toString(): String {
            return "\n description : $description"
        }
    }
}