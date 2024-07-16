package com.cuebit.io.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cuebit.io.R

class TaskFrequencyAdapter : RecyclerView.Adapter<TaskFrequencyAdapter.DayViewHolder>() {

    private val days = listOf("M", "T", "W", "T", "F", "S", "S")
    private val selectedDays = BooleanArray(days.size) { true }
    private val selectedDayList = mutableListOf<String>().apply {
        addAll(days) // Initially add all days since all are selected by default
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.frequency_day_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position], selectedDays[position])
        holder.itemView.setOnClickListener {
            selectedDays[position] = !selectedDays[position]
            if (selectedDays[position]) {
                selectedDayList.add(days[position])
            } else {
                selectedDayList.remove(days[position])
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = days.size

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayInitial: TextView = itemView.findViewById(R.id.day_initial)

        fun bind(day: String, isSelected: Boolean) {
            dayInitial.text = day
            dayInitial.isSelected = isSelected
            val backgroundColor = if (isSelected) {
                ContextCompat.getColor(itemView.context, R.color.dark_purple)
            } else {
                ContextCompat.getColor(itemView.context, R.color.bg_white)
            }
            val textColor = if (isSelected) {
                ContextCompat.getColor(itemView.context, R.color.white)
            } else {
                ContextCompat.getColor(itemView.context, R.color.black)
            }
            dayInitial.setTextColor(textColor)
            dayInitial.setBackgroundColor(backgroundColor)
        }
    }

    fun getSelectedDays(): List<String> {
        return selectedDayList
    }
}
