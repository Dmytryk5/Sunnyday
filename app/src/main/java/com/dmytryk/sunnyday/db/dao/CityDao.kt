package com.dmytryk.sunnyday.db.dao

import androidx.room.*
import com.dmytryk.sunnyday.db.entity.CityForecast
import com.dmytryk.sunnyday.db.entity.CityWeather
import com.dmytryk.sunnyday.db.entity.Forecast
import io.reactivex.Single

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city: CityWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForecasts(forecasts: List<Forecast>)

//    @Query("SELECT * FROM weather")
//    fun getCities(): List<CityWeather>
//
//    @Query("SELECT * FROM weather WHERE name=:cityName ")
//    fun getCity(cityName: String):CityWeather

    @Transaction
    @Query("SELECT * FROM weather")
    fun getAllCitiesForecasts(): Single<List<CityForecast>>

    @Transaction
    @Query("SELECT * FROM weather WHERE name=:cityName ")
    fun getCityForecast(cityName: String): Single<CityForecast>

    @Query("DELETE FROM forecast WHERE cityName=:city ")
    fun deleteForecastByCity(city: String)

}