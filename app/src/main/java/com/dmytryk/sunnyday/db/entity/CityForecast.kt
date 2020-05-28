package com.dmytryk.sunnyday.db.entity

import androidx.room.Embedded
import androidx.room.Relation


data class CityForecast(
    @Embedded val cityWeather: CityWeather
) {
    @Relation(
        parentColumn = "name",
        entityColumn = "cityName"
    ) lateinit var forecast: List<Forecast>
}
