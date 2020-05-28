package com.dmytryk.sunnyday.network.response

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("dt") val time: Long,
    @SerializedName("temp") val temperature: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("wind_speed") val windSpeed: Double,
    @SerializedName("weather") val weatherCondition: List<WeatherConditionDto>
)