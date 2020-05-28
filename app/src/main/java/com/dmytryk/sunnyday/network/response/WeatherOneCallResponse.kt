package com.dmytryk.sunnyday.network.response

import com.google.gson.annotations.SerializedName

data class WeatherOneCallResponse(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double,
    @SerializedName("current") val current: WeatherDto,
    @SerializedName("hourly") val hourly: List<WeatherDto>,
    @SerializedName("daily") val daily: List<DailyWeatherDto>
)