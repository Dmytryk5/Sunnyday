package com.dmytryk.sunnyday.ui

import android.app.AlertDialog
import android.location.Geocoder
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.marginStart
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dmytryk.sunnyday.R
import com.dmytryk.sunnyday.data.City
import com.dmytryk.sunnyday.data.ResponseData
import com.dmytryk.sunnyday.extensions.logD
import com.dmytryk.sunnyday.extensions.makeItGone
import com.dmytryk.sunnyday.extensions.makeVisible
import com.dmytryk.sunnyday.ui.recycler.CityAdapter
import com.dmytryk.sunnyday.viewmodel.CityViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_cities.*
import java.util.*


class CitiesFragment : Fragment(R.layout.fragment_cities) {

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[CityViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleArguments()
        setupAddCityButton()
        getCities()
    }

    private fun setupAddCityButton() {
        fab.setOnClickListener {

            val editTextCityName = EditText(requireContext())
            editTextCityName.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            editTextCityName.hint = getString(R.string.city_town_village)


            AlertDialog.Builder(requireContext())
                .setView(editTextCityName)
                .setTitle(getString(R.string.add_city))
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    val text = editTextCityName.text.toString()
                    if (text.isNotBlank()) {
                        try {
                            createCity(text)
                        } catch (exception: Exception){
                            exception.printStackTrace()
                            dialog.dismiss()
                            Snackbar.make(fab, getString(R.string.city_not_found), Snackbar.LENGTH_LONG).show()

                        }
                    }
                    dialog.dismiss()
                }.setNegativeButton(android.R.string.cancel){dialog, which ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun createCity(cityName: String) {

            val geo = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geo.getFromLocationName(cityName, 1)
            logD("city $cityName address ${addresses.get(0).locality},${addresses.get(0)}, " +
                    "coords ${addresses[0].latitude} - ${addresses[0].longitude} ")
            val lat = addresses[0].latitude
            val lon = addresses[0].longitude

            viewModel.addCity(cityName, lat, lon)
    }

    private fun getCities() {
        val adapter = CityAdapter{
            goToForecast(it)
        }
        recyclerViewCities.adapter = adapter

        viewModel.cityLiveData.observe(viewLifecycleOwner, Observer {
            when (it){
                is ResponseData.Loading -> progressCity.makeVisible()
                is ResponseData.SuccessResponse -> adapter.setNewItems(it.data!!).also { progressCity.makeItGone() }
                is ResponseData.ErrorResponse -> Snackbar.make(progressCity, it.error?.message.toString(), Snackbar.LENGTH_LONG).also { progressCity.makeItGone() }
            }
            if (it is ResponseData.SuccessResponse) {
                adapter.setNewItems(it.data!!)
            }
        })

        viewModel.getCities()

    }

    private fun goToForecast(city: City) {
        val args = bundleOf(
            Pair(ForecastFragment.LAT_KEY, city.lat),
            Pair(ForecastFragment.LON_KEY, city.lon),
            Pair(ForecastFragment.CITY_KEY, city.title)
        )

        findNavController().navigate(R.id.ForecastFragment, args)
    }

    //shows error snackbar if needed
    private fun handleArguments() {
        val args = arguments
        if (args != null){
            if (args.getString(KEY_CITY_ERROR) == VALUE_CANT_GET_COORDINATES){
                Snackbar.make(fab, R.string.coordinates_error_snackbar, Snackbar.LENGTH_LONG).show()
                args.clear()
            }
        } else {
            logD("arguments are null. no error")
        }
    }

    companion object {
        const val KEY_CITY_ERROR = "city.arguments.ERROR"
        const val VALUE_CANT_GET_COORDINATES = "city.arguments.value.CANT_GET_COORDINATES"
    }

}
