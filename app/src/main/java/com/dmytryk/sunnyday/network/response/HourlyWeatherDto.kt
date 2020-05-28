package com.dmytryk.sunnyday.network.response

import com.google.gson.annotations.SerializedName

data class HourlyWeatherDto(
    @SerializedName("dt") val time: String,
    @SerializedName("temp") val temperature: String
)