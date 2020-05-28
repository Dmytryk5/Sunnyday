package com.dmytryk.sunnyday.network.response

import com.google.gson.annotations.SerializedName

data class DailyWeatherDto(
    @SerializedName("dt") val time: Long,
    @SerializedName("temp") val dailyTemp: DailyTemperatureDto,
    @SerializedName("weather") val weatherCondition: List<WeatherConditionDto>
)