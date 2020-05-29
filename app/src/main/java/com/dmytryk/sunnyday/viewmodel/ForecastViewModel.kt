package com.dmytryk.sunnyday.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmytryk.sunnyday.data.CurrentWeather
import com.dmytryk.sunnyday.data.CurrentWeatherCondition
import com.dmytryk.sunnyday.data.ForecastWeather
import com.dmytryk.sunnyday.data.ResponseData
import com.dmytryk.sunnyday.extensions.toIconUrl
import com.dmytryk.sunnyday.network.api.WeatherApi
import com.dmytryk.sunnyday.provider.ApiProvider
import com.dmytryk.sunnyday.extensions.logD
import com.dmytryk.sunnyday.extensions.logE
import com.dmytryk.sunnyday.provider.RepositoryProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import retrofit2.HttpException

class ForecastViewModel : ViewModel(){

    private val repository by lazy {
        RepositoryProvider.repository
    }

    private val compositeDisposable = CompositeDisposable()

    private val _forecast = MediatorLiveData<ResponseData<List<ForecastWeather>>>()
    val forecastLiveData : LiveData<ResponseData<List<ForecastWeather>>> = _forecast

    private val _conditions = MediatorLiveData<ResponseData<List<CurrentWeatherCondition>>>()
    val conditionLiveData : LiveData<ResponseData<List<CurrentWeatherCondition>>> = _conditions

    private val _weather = MediatorLiveData<ResponseData<CurrentWeather>>()
    val weatherLiveData = _weather


    fun getWeather(lat: Double, lon: Double, city: String, units: String = "metric"){
//        _conditions.value = ResponseData.Loading()
//        _weather.value = ResponseData.Loading()

        repository.addCity(city, lat, lon).subscribe ({
            _weather.addSource(repository.getCurrentWeather(city)){
                _weather.value = it
            }

            _conditions.addSource(repository.getCurrentWeatherConditions(city)){
                _conditions.value = it
            }

            _forecast.addSource(repository.getForecast(city)){
                _forecast.value = it
            }
        }, {
            _weather.addSource(repository.getCurrentWeather(city)){
                _weather.value = it
            }

            _conditions.addSource(repository.getCurrentWeatherConditions(city)){
                _conditions.value = it
            }

            _forecast.addSource(repository.getForecast(city)){
                _forecast.value = it
            }
        }).addTo(compositeDisposable)


    }

    override fun onCleared() {
        super.onCleared()
        repository.clear()
        compositeDisposable.clear()

    }

}