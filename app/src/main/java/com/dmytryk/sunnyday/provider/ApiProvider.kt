package com.dmytryk.sunnyday.provider

import com.dmytryk.sunnyday.network.api.WeatherApi


object ApiProvider {

    val weatherApi : WeatherApi by lazy {
        RetrofitProvider.retrofit.create(WeatherApi::class.java)
    }

}