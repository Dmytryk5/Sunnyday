package com.dmytryk.sunnyday.ui.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dmytryk.sunnyday.R
import com.dmytryk.sunnyday.data.City
import com.dmytryk.sunnyday.extensions.toIconUrl
import kotlinx.android.synthetic.main.list_item_city.view.*


class CityAdapter(private val onCityClicked: (City)->Unit): RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    private val items = mutableListOf<City>()

    fun setNewItems(newItems: List<City>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder.getInstance(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.cardCity.setOnClickListener {
            onCityClicked.invoke(items[position])
        }
    }


    class CityViewHolder private constructor(view: View): RecyclerView.ViewHolder(view) {

        fun bind(city: City){

            itemView.apply {
                textViewCity.text = city.title
                val min = context.getString(R.string.temperature_celsius, city.minTemp)
                val max = context.getString(R.string.temperature_celsius, city.maxTemp)
                val temperatureText = "$min/$max"
                textViewCityTemperature.text = temperatureText

                Glide.with(context).load(city.icon.toIconUrl()).into(imageViewCityWeatherIcon)
            }
        }

        companion object {
            fun getInstance(parent: ViewGroup): CityViewHolder{
                return CityViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_city, parent, false)
                )
            }
        }
    }
}