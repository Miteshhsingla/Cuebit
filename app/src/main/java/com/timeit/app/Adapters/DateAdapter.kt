package com.timeit.app.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.timeit.app.DataModels.Day
import com.timeit.app.R

class DateAdapter(private var dateItemList: List<Day>) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = -1
    private var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val day = dateItemList[position]
        holder.dayOfWeekText.text = day.dayOfWeek
        holder.dayNumberText.text = day.dayNumber.toString()

        if (day.isToday) {
            holder.block.setBackgroundResource(R.drawable.selected_day_bg)
            holder.dayOfWeekText.setTextColor(Color.WHITE)
            holder.dayNumberText.setTextColor(Color.WHITE)
            holder.todayDot.isVisible = true
        } else {
            holder.block.setBackgroundColor(Color.TRANSPARENT)
            holder.dayOfWeekText.setTextColor(Color.BLACK)
            holder.dayNumberText.setTextColor(Color.BLACK)
            holder.todayDot.isVisible = false
        }

        holder.block.setOnClickListener {
            updateSelected(position)
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return dateItemList.size
    }

    fun updateSelected(position: Int) {
        if (selectedPosition != position) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeekText: TextView = itemView.findViewById(R.id.weekdayText)
        val dayNumberText: TextView = itemView.findViewById(R.id.weekdayNumber)
        val todayDot: ImageView = itemView.findViewById(R.id.todayDot)
        val block: ConstraintLayout = itemView as ConstraintLayout
    }
}
