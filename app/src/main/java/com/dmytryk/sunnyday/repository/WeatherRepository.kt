package com.dmytryk.sunnyday.repository

import androidx.lifecycle.LiveData
import com.dmytryk.sunnyday.data.*
import io.reactivex.Completable

interface WeatherRepository {
    fun addCity(cityName: String, lat: Double, lon: Double):Completable
    fun getCities(): LiveData<ResponseData<List<City>>>
    fun getCurrentWeather(city: String): LiveData<ResponseData<CurrentWeather>>
    fun getCurrentWeatherConditions(city: String): LiveData<ResponseData<List<CurrentWeatherCondition>>>
    fun getForecast(city: String): LiveData<ResponseData<List<ForecastWeather>>>
    fun updateCities(): Completable
    fun clear()
}