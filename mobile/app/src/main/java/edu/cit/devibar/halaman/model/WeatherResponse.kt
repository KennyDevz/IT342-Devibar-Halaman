package edu.cit.devibar.halaman.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("temperature")
    val temperature: Double,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("isDay")
    val isDay: Boolean,

    @SerializedName("weatherCode")
    val weatherCode: Int,

    @SerializedName("location")
    val location: String
)