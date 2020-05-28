package com.dmytryk.sunnyday.ui.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dmytryk.sunnyday.R
import com.dmytryk.sunnyday.data.ForecastWeather
import com.dmytryk.sunnyday.extensions.toIconUrl
import kotlinx.android.synthetic.main.list_item_weather_forecast.view.*

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private val items = mutableListOf<ForecastWeather>()

    fun setNewItems(newItems: List<ForecastWeather>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder.getInstance(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(items[position])
    }


    class ForecastViewHolder private constructor(view: View): RecyclerView.ViewHolder(view) {

        fun bind(weather: ForecastWeather){
            itemView.apply {
                val temperatureText = context.getString(R.string.temperature_celsius, weather.temp)
                textViewTemperature.text = temperatureText
                textViewDay.text = weather.day
                textViewHour.text = weather.hour

                Glide.with(context).load(weather.icon.toIconUrl()).into(imageViewWeatherIcon)
            }
        }

        companion object {
            fun getInstance(parent: ViewGroup): ForecastViewHolder{
                return ForecastViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_weather_forecast, parent, false)
                )
            }
        }
    }
}