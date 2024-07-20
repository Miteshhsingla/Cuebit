package com.cuebit.io.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cuebit.io.R
import java.util.ArrayList
import java.util.Calendar

class TaskFrequencyAdapter(selectedFrequency: ArrayList<String>?) : RecyclerView.Adapter<TaskFrequencyAdapter.DayViewHolder>() {

    private val days = listOf("M", "T", "W", "T", "F", "S", "S")
    private val selectedDays = BooleanArray(days.size) { false }
    private val currentDayIndex = getCurrentDayIndex()
    private val selectedDayList = mutableListOf<String>().apply {
        addAll(days) // Initially add all days since all are selected by default
    }

    init {
        selectedDays[currentDayIndex] = true // Highlight the current day by default
        selectedDayList.add(days[currentDayIndex])

        // Highlight days from selectedFrequency if it's not null
        selectedFrequency?.forEach { frequency ->
            days.indexOf(frequency).takeIf { it >= 0 }?.let { index ->
                selectedDays[index] = true
                if (!selectedDayList.contains(frequency)) {
                    selectedDayList.add(frequency)
                }
            }
        }
    }

    private fun getCurrentDayIndex(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> 6
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 0
        } // Adjusting for list index starting at 0
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
