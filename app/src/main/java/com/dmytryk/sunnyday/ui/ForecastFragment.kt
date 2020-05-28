package com.dmytryk.sunnyday.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dmytryk.sunnyday.R
import com.dmytryk.sunnyday.data.CurrentWeather
import com.dmytryk.sunnyday.data.CurrentWeatherCondition
import com.dmytryk.sunnyday.data.ForecastWeather
import com.dmytryk.sunnyday.data.ResponseData
import com.dmytryk.sunnyday.extensions.*
import com.dmytryk.sunnyday.ui.recycler.ConditionsAdapter
import com.dmytryk.sunnyday.ui.recycler.ForecastAdapter
import com.dmytryk.sunnyday.viewmodel.ForecastViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_forecast.*
import java.util.*

class ForecastFragment : Fragment(R.layout.fragment_forecast) {

    private val viewModel by lazy {
        ViewModelProvider(this)[ForecastViewModel::class.java]
    }

    private var latitude = 0.0
    private var longitude = 0.0
    private var cityName = ""

    private val conditionsAdapter = ConditionsAdapter()
    private val forecastAdapter = ForecastAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        resolveCoordinates()
        setupAdapters()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        logD("permissionResult $requestCode")
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                logD("permission granted")
                getCoordinatesAndWeather()
            } else {
                goToCitiesWithError()
            }
        }
    }

    private fun getCoordinatesAndWeather() {
        getDeviceCoordinates()
        if (latitude == 0.0 && longitude == 0.0){
            logE("coordinates are not set")
        } else {
            getCityName()
            getWeather()
        }
    }

    private fun getCityName() {
        val geo = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geo.getFromLocation(latitude, longitude, 1)
        val city = addresses[0].locality
        this.cityName = city
        setDownloadingCity(cityName)
        logD("current city: $cityName")//, country $countryName, state : $stateName")
    }

    private fun setDownloadingCity(address: String) {
        val forecastText = getString(R.string.getting_forecast_for, address)
        textViewCityName.text = forecastText
    }

    private fun getDeviceCoordinates() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        try {

            val provider = locationManager.getBestProvider(criteria, false)
            if (provider != null) {
                val coordinates = locationManager.getLastKnownLocation(provider)
                logD("coordinates ${coordinates?.latitude} ${coordinates?.longitude}")
                if (coordinates != null){
                    latitude = coordinates.latitude
                    longitude = coordinates.longitude
                } else {
                    goToCitiesWithError()
                }
            }
        } catch (securityException : SecurityException){
            logE("cannot get coordinates due to Security Exception")
            securityException.printStackTrace()
        }
    }

    private fun setupToolbar() {
        fabCities.setOnClickListener {
            goToCities()
        }
    }

    private fun setupAdapters() {
        recyclerViewWeatherConditions.adapter = conditionsAdapter
        recyclerViewForecast.adapter = forecastAdapter
    }

    private fun resolveCoordinates() {

        if(coordinatesPassed()){
            logD("coordinates passed")
            getWeather()
            return
        }

        if (checkGPSPermission()){
            logD("permission granted")
            getCoordinatesAndWeather()
        } else {
            logD("permission denied")
            showDialogLocationRequiredDialog()
        }

    }

    private fun requestGpsPermission() {
        requestPermissions(listOf(Manifest.permission.ACCESS_FINE_LOCATION).toTypedArray(),
            PERMISSION_REQUEST_CODE)
    }

    private fun goToCitiesWithError(){
        val bundle = Bundle()
        bundle.putString(CitiesFragment.KEY_CITY_ERROR, CitiesFragment.VALUE_CANT_GET_COORDINATES)
        findNavController().navigate(R.id.CitiesFragment, bundle)
    }

    private fun goToCities(){
        findNavController().navigate(R.id.CitiesFragment)
    }

    private fun getWeather() {
        viewModel.conditionLiveData.observe(viewLifecycleOwner, Observer {
            when (it){
                is ResponseData.Loading -> progressBarWeather.makeVisible()
                is ResponseData.SuccessResponse -> handleSuccessConditions(it.data!!)
                is ResponseData.ErrorResponse -> handleError(it.error)
            }
        })

        viewModel.weatherLiveData.observe(viewLifecycleOwner, Observer {
            when (it){
                is ResponseData.Loading -> progressBarWeather.makeVisible()
                is ResponseData.SuccessResponse -> handleSuccess(it.data!!)
                is ResponseData.ErrorResponse -> handleError(it.error)
            }
        })

        viewModel.forecastLiveData.observe(viewLifecycleOwner, Observer {
            when (it){
                is ResponseData.Loading -> progressBarWeather.makeVisible()
                is ResponseData.SuccessResponse -> handleSuccessForecast(it.data!!)
                is ResponseData.ErrorResponse -> handleError(it.error)
            }
        })

        viewModel.getWeather(latitude, longitude, cityName)
    }

    private fun handleSuccessForecast(data: List<ForecastWeather>) {
        forecastAdapter.setNewItems(data)
    }

    private fun handleSuccess(weather: CurrentWeather) {
        progressBarWeather.makeItGone()
        Glide.with(requireContext()).load(weather.icon.toIconUrl()).into(imageViewWeatherIconBig)
        val temperature = getString(R.string.temperature_celsius, weather.temperature.toString())
        textViewCurrentTemperature.text = temperature
        textViewCityName.text = cityName
    }

    private fun handleError(error: Throwable?) {
        progressBarWeather.makeItGone()
        textViewCityName.text = ""
        if (error != null)
            Snackbar.make(progressBarWeather, error.message ?:
            getString(R.string.something_went_horribly_wrong),
                Snackbar.LENGTH_LONG).show()
    }

    private fun handleSuccessConditions(data: List<CurrentWeatherCondition>) {
        conditionsAdapter.setNewItems(data)
    }

    private fun coordinatesPassed(): Boolean {
        var lat: Double? = null
        var lon: Double? = null
        var city = ""


        arguments?.let {
            lat = it.getDouble(LAT_KEY, 0.0)
            lon = it.getDouble(LON_KEY, 0.0)
            city = it.getString(CITY_KEY, "")
        }

        if (lat != null && lon != null){
            this.latitude = lat!!
            this.longitude = lon!!
            this.cityName = city
        }

        return !(this.latitude == 0.0 || this.longitude == 0.0)
    }

    private fun showDialogLocationRequiredDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_required))
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                dialog.dismiss()
            }.setMessage(getString(R.string.permission_explanation))
            .setOnDismissListener {
                logD("dissmissed")
                requestGpsPermission()
            }
            .show()

    }

    private fun checkGPSPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        const val LAT_KEY = "argument.Latitude"
        const val LON_KEY = "argument.Longitude"
        const val CITY_KEY = "argument.City"
        const val PERMISSION_REQUEST_CODE = 42
    }

}
