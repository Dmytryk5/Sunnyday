package com.dmytryk.sunnyday.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dmytryk.sunnyday.network.response.WeatherOneCallResponse

@Entity(tableName = "weather")
data class CityWeather(
    @PrimaryKey val name: String,
    val lat: Double,
    val lon: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val icon: String,
    @Embedded val currentWeather : CurrentCityWeather
) {
    companion object {
        fun fromResponse(
            response: WeatherOneCallResponse,
            cityName: String
        ):CityWeather{
            val minTemperature = response.daily.firstOrNull()?.dailyTemp?.min ?: -100.0
            val maxTemperature = response.daily.firstOrNull()?.dailyTemp?.max ?: 100.0
            val icon = response.daily.firstOrNull()?.weatherCondition?.firstOrNull()?.icon ?: ""

            val currentWeather = CurrentCityWeather(
                response.current.temperature,
                response.current.weatherCondition.firstOrNull()?.icon ?: "",
                response.current.pressure,
                response.current.windSpeed,
                response.current.clouds,
                response.current.humidity
            )

            return CityWeather(cityName,
                response.latitude, response.longitude,
                minTemperature, maxTemperature,
                icon, currentWeather
            )
        }
    }
}

