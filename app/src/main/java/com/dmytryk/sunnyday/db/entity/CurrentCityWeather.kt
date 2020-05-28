package com.dmytryk.sunnyday.db.entity

data class CurrentCityWeather(
    val temperature: Double,
    val currentIcon: String,
    val pressure: Int,
    val windSpeed: Double,
    val clouds: Int,
    val humidity: Int
)