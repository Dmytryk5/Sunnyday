package com.dmytryk.sunnyday.provider

import androidx.room.Room
import com.dmytryk.sunnyday.app.SunnydayApp
import com.dmytryk.sunnyday.db.WeatherDatabase
import com.dmytryk.sunnyday.db.dao.CityDao

object DatabaseProvider {
    val database by lazy {
        Room.databaseBuilder(
            SunnydayApp.instance.applicationContext,
            WeatherDatabase::class.java,
            "weather.db")
            .build()
    }

    val cityDao : CityDao by lazy {
        database.cityDao()
    }
}