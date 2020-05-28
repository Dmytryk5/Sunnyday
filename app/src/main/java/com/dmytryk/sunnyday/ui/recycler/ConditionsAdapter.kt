package com.dmytryk.sunnyday.ui.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmytryk.sunnyday.R
import com.dmytryk.sunnyday.data.CurrentWeatherCondition
import kotlinx.android.synthetic.main.list_item_weather_condition.view.*

class ConditionsAdapter : RecyclerView.Adapter<ConditionsAdapter.ConditionsViewHolder>() {

    private val items = mutableListOf<CurrentWeatherCondition>()

    fun setNewItems(newItems: List<CurrentWeatherCondition>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConditionsViewHolder {
        return ConditionsViewHolder.getInstance(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ConditionsViewHolder, position: Int) {
        holder.bind(items[position])
    }


    class ConditionsViewHolder private constructor(view: View): RecyclerView.ViewHolder(view) {

        fun bind(weather: CurrentWeatherCondition){

            itemView.apply {
                textViewConditionLabel.text = weather.title
                textViewConditionValue.text = weather.value
                imageViewConditionIcon.setImageResource(weather.icon)
            }
        }

        companion object {
            fun getInstance(parent: ViewGroup): ConditionsViewHolder{
                return ConditionsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_weather_condition, parent, false)
                )
            }
        }
    }
}