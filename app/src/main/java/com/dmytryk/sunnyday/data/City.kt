package com.dmytryk.sunnyday.data

import com.dmytryk.sunnyday.db.entity.CityForecast

data class City(val lat: Double,
                val lon: Double,
                val title: String,
                val minTemp: String,
                val maxTemp: String,
                val icon: String) {

    companion object {
        fun fromEntity(entity: CityForecast): City{
            return City(entity.cityWeather.lat, entity.cityWeather.lon, entity.cityWeather.name,
                entity.cityWeather.minTemperature.toString(),
                entity.cityWeather.maxTemperature.toString(),
                entity.cityWeather.icon
            )
        }
    }
}