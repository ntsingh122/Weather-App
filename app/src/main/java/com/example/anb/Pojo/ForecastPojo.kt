package com.example.anb.Pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForecastPojo {
    @SerializedName("cod")
    @Expose
    var cod: String? = null

    @SerializedName("message")
    @Expose
    var message: Double? = null

    @SerializedName("cnt")
    @Expose
    var cnt: Int? = null

    @SerializedName("list")
    @Expose
    lateinit var list: kotlin.collections.List<List>

    @SerializedName("city")
    @Expose
    var city: City? = null

    override fun toString(): String {
        return """
 City ${city.toString()}$list"""
    }

    //.replace("[","\n ").replace("]","").replace(",","")
    //@Override
    //public String toString() {
    //    return "ClassPojo [dt = " + dt + "+ visibility = " + visibility + "+ timezone = " + timezone + "+ weather = " + weather + "+ name = " + name + "+ cod = " + cod + "+ main = " + main + "+ id = " + id + "]";
    //}
    inner class City {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("coord")
        @Expose
        var coord: Coord? = null

        @SerializedName("country")
        @Expose
        var country: String? = null

        override fun toString(): String {
            return "name : $name"
        }
    }

    inner class Coord {
        @SerializedName("lat")
        @Expose
        var lat: Double? = null

        @SerializedName("lon")
        @Expose
        var lon: Double? = null

        override fun toString(): String {
            return """
 lat : ${lat.toString()}
 lon : $lon"""
        }
    }

    inner class List {
        @SerializedName("dt")
        @Expose
        var dt: Int? = null

        @SerializedName("main")
        @Expose
        lateinit var main: CurrentDayPojo.Main

        @SerializedName("weather")
        @Expose
        lateinit var weather: kotlin.collections.List<CurrentDayPojo.Weather>

        @SerializedName("wind")
        @Expose
        lateinit var wind: Wind

        // return dtTxt.substring(0,11);
        @SerializedName("dt_txt")
        @Expose
        lateinit var dtTxt: String

        override fun toString(): String {
            return """$main$weather$wind
 Date : $dtTxt
"""
        }
    }

    class Wind {

        @JvmField
        var speed: Double = 0.0


        @JvmField
        var deg: Double = 0.0
        override fun toString(): String {
            return """
 Speed : $speed
 Degree : $deg"""
        }
    }
}