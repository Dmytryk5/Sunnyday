package com.dmytryk.sunnyday.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dmytryk.sunnyday.data.*
import com.dmytryk.sunnyday.db.entity.CityForecast
import com.dmytryk.sunnyday.db.entity.CityWeather
import com.dmytryk.sunnyday.db.entity.Forecast
import com.dmytryk.sunnyday.extensions.logD
import com.dmytryk.sunnyday.extensions.logE
import com.dmytryk.sunnyday.network.response.WeatherOneCallResponse
import com.dmytryk.sunnyday.provider.ApiProvider
import com.dmytryk.sunnyday.provider.DatabaseProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class WeatherRepositoryImpl : WeatherRepository {

    private val cityDao by lazy {
        DatabaseProvider.cityDao
    }
    private val weatherApi by lazy {
        ApiProvider.weatherApi
    }

    private val compositeDisposable = CompositeDisposable()


    override fun addCity(
        cityName: String,
        lat: Double,
        lon: Double
    ) : Completable {


        return Completable.create {emitter->
            val disposable = weatherApi
                .getWeatherOneCall(lat.toString(), lon.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    compositeDisposable.add(Single.fromCallable {
                        saveResponseToDb(it, cityName)
                    }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            logD("success insert")
                            emitter.onComplete()
                        }, {
                            logE("error in insert")
                            it.printStackTrace()
                        }))
                }, {
                    logE("could not save city ${it.message}")
                    it.printStackTrace()
                })

            compositeDisposable.add(disposable)
        }
    }

    private fun saveResponseToDb(response: WeatherOneCallResponse, cityName: String) {

        val cityWeather = CityWeather.fromResponse(response, cityName)
        val cityForecast = CityForecast(cityWeather).apply {
            forecast = response.getForecast(cityName)
        }

        cityDao.insertCity(cityForecast.cityWeather)
        cityDao.deleteForecastByCity(cityName)
        cityDao.insertForecasts(cityForecast.forecast)


    }

    override fun getCities(): LiveData<ResponseData<List<City>>> {
        logD("cities")
        val citiesLiveData = MutableLiveData<ResponseData<List<City>>>()
        val disposable = cityDao.getAllCitiesForecasts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val cities = it.map {entity->
                    City.fromEntity(entity)
                }
                val responseData = ResponseData.SuccessResponse(cities)
                citiesLiveData.value = responseData
            },{
                it.printStackTrace()
                citiesLiveData.value = ResponseData.ErrorResponse(it)
            })

        compositeDisposable.add(disposable)

        return citiesLiveData
    }

    override fun getCurrentWeather(
        city: String
    ): LiveData<ResponseData<CurrentWeather>> {
        val cityLiveData = MutableLiveData<ResponseData<CurrentWeather>>()
        cityLiveData.value = ResponseData.Loading()
        val disposable = cityDao.getCityForecast(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val currentWeather = CurrentWeather(it.cityWeather.currentWeather.temperature,
                    it.cityWeather.icon)
                cityLiveData.value = ResponseData.SuccessResponse(currentWeather)
            },{
                it.printStackTrace()
                cityLiveData.value = ResponseData.ErrorResponse(it)
            })

        compositeDisposable.add(disposable)

        return cityLiveData
    }

    override fun getCurrentWeatherConditions(
        city: String
    ): LiveData<ResponseData<List<CurrentWeatherCondition>>> {
        val currentWeatherLiveData = MutableLiveData<ResponseData<List<CurrentWeatherCondition>>>()

        val disposable = cityDao.getCityForecast(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val currentConditions = mutableListOf<CurrentWeatherCondition>()
                currentConditions.add(CurrentWeatherCondition.getPressure(it.cityWeather.currentWeather.pressure.toString()))
                currentConditions.add(CurrentWeatherCondition.getWind(it.cityWeather.currentWeather.windSpeed.toString()))
                currentConditions.add(CurrentWeatherCondition.getHumidity(it.cityWeather.currentWeather.humidity.toString()))
                currentConditions.add(CurrentWeatherCondition.getClouds(it.cityWeather.currentWeather.clouds.toString()))

                currentWeatherLiveData.value = ResponseData.SuccessResponse(currentConditions)
            },{
                it.printStackTrace()
                currentWeatherLiveData.value = ResponseData.ErrorResponse(it)
            })

        compositeDisposable.add(disposable)

        return currentWeatherLiveData
    }

    override fun getForecast(
        city: String
    ): LiveData<ResponseData<List<ForecastWeather>>> {
        val forecastLiveData = MutableLiveData<ResponseData<List<ForecastWeather>>>()
        forecastLiveData.value = ResponseData.Loading()

        val disposable = cityDao.getCityForecast(city).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ cityForecast ->
                val forecast = cityForecast.forecast.map {
                    val calendar = Calendar.getInstance(Locale.ROOT).apply {
                        timeInMillis = it.time * 1000L

                    }

                    logD("date: ${calendar.time} time in millist ${calendar.timeInMillis}")

                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ROOT) ?: "Monday"
                    ForecastWeather(it.forecastIcon, it.temperature.toString(), day, "$hour:00" )
                }

                forecastLiveData.value = ResponseData.SuccessResponse(forecast)
            },{
                it.printStackTrace()
                forecastLiveData.value = ResponseData.ErrorResponse(it)
            })

        compositeDisposable.add(disposable)

        return forecastLiveData
    }

    override fun updateCities(): Completable {
        val completable = Completable.create {emitter ->
            val disposable = cityDao.getAllCitiesForecasts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logD("city list ")
                    it.forEachIndexed { index, city->
                        logD("city ${city.cityWeather.name}")
                        val nestedDisposable = weatherApi.getWeatherOneCall(city.cityWeather.lat.toString(), city.cityWeather.lon.toString())
                            .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({response ->
                                logD("got response for city ${city.cityWeather.name}")
                                saveResponseToDb(response, city.cityWeather.name)
                                emitter.onComplete()
                            },{error ->
                                error.printStackTrace()
                            })

                        compositeDisposable.add(nestedDisposable)
                    }
                },{
                    it.printStackTrace()
                })

            compositeDisposable.add(disposable)

        }

        return completable
    }

    private fun WeatherOneCallResponse.getForecast(cityName: String): List<Forecast>{
        return this.hourly.map {
            Forecast(it.temperature, it.time, it.weatherCondition.firstOrNull()?.icon ?: "01d", cityName )
        }
    }

    override fun clear() {
        compositeDisposable.clear()
    }
}