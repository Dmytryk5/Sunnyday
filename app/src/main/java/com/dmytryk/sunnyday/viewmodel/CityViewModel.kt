package com.dmytryk.sunnyday.viewmodel

import androidx.lifecycle.*
import com.dmytryk.sunnyday.data.City
import com.dmytryk.sunnyday.data.ResponseData
import com.dmytryk.sunnyday.extensions.logD
import com.dmytryk.sunnyday.provider.RepositoryProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CityViewModel : ViewModel(){

    private val repository by lazy {
        RepositoryProvider.repository
    }
    private val compositeDisposable = CompositeDisposable()

    private val _cities = MediatorLiveData<ResponseData<List<City>>>()
    val cityLiveData : LiveData<ResponseData<List<City>>> = _cities


    fun getCities(){

        if (_cities.value == null)
            repository.updateCities().observeOn(AndroidSchedulers.mainThread()).subscribe {
                _cities.addSource(repository.getCities()){
                    _cities.value = it
                }
            }.addTo(compositeDisposable)


    }

    fun addCity(cityName: String, lat: Double, lon: Double) {

        repository.addCity(cityName, lat, lon).subscribe{
            _cities.addSource(repository.getCities()){
                _cities.value = it
            }
        }.addTo(compositeDisposable)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        repository.clear()
    }
}