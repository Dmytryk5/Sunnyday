package com.dmytryk.sunnyday.data

import androidx.annotation.DrawableRes
import com.dmytryk.sunnyday.R
import com.dmytryk.sunnyday.app.SunnydayApp

data class CurrentWeatherCondition(
    @DrawableRes val icon: Int,
    val title: String,
    val value: String
){
    companion object {
        fun getPressure(pressure: String): CurrentWeatherCondition{
            return CurrentWeatherCondition(R.drawable.ic_pressure,
                SunnydayApp.instance.applicationContext.getString(R.string.pressure),
                "$pressure hPa")
        }

        fun getWind(wind: String): CurrentWeatherCondition{
            return CurrentWeatherCondition(R.drawable.ic_wind,
                SunnydayApp.instance.applicationContext.getString(R.string.wind),
                wind
            )
        }

        fun getHumidity(humidity: String): CurrentWeatherCondition{
            return CurrentWeatherCondition(R.drawable.ic_humidity,
                SunnydayApp.instance.applicationContext.getString(R.string.humidity),
                "$humidity %"
            )
        }

        fun getClouds(cloudiness: String): CurrentWeatherCondition{
            return CurrentWeatherCondition(R.drawable.ic_cloudy,
                SunnydayApp.instance.applicationContext.getString(R.string.cloudiness),
                "$cloudiness %"
            )
        }
    }
}