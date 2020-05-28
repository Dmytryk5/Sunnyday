package com.dmytryk.sunnyday.provider

import com.dmytryk.sunnyday.repository.WeatherRepository
import com.dmytryk.sunnyday.repository.WeatherRepositoryImpl

object RepositoryProvider {
    val repository: WeatherRepository by lazy {
        WeatherRepositoryImpl()
    }
}