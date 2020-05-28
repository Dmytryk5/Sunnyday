package com.dmytryk.sunnyday.network.response

import com.google.gson.annotations.SerializedName

data class DailyTemperatureDto(
    @SerializedName("day") val day: Double,
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double
)