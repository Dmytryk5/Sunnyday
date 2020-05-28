package com.dmytryk.sunnyday.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dmytryk.sunnyday.db.dao.CityDao
import com.dmytryk.sunnyday.db.entity.CityForecast
import com.dmytryk.sunnyday.db.entity.CityWeather
import com.dmytryk.sunnyday.db.entity.Forecast

@Database(entities = arrayOf(CityWeather::class, Forecast::class), version = 2)
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun cityDao(): CityDao
}