package com.dmytryk.sunnyday.network.api

import com.dmytryk.sunnyday.network.response.WeatherOneCallResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi{

    @GET("data/2.5/onecall")
    fun getWeatherOneCall(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units : String = "metric",
        @Query("appid") apiKey : String = API_KEY) : Single<WeatherOneCallResponse>

    companion object{
        const val API_KEY = "781f3fc18ed4cf55a9ddf3b6e8369159"
    }
}