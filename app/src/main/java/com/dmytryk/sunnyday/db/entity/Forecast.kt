package com.dmytryk.sunnyday.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "forecast", primaryKeys = arrayOf("temperature", "time"))
data class Forecast(
    val temperature: Double,
    val time: Long,
    val forecastIcon: String,
    val cityName: String //foreign key
)
