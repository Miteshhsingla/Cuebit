package com.timeit.app.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.timeit.app.DataModels.HabitDataModel
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.R

class HabitsAdapter(var habitsList: MutableList<HabitDataModel>, var context: Context): RecyclerView.Adapter<HabitsAdapter.habitViewHolder>() {

    class habitViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val image: ImageView = itemView.findViewById(R.id.imageIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): habitViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return habitViewHolder(view)
    }

    fun getItem(position: Int): HabitDataModel {
        return habitsList[position]
    }

    fun removeItem(position: Int) {
        habitsList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return habitsList.size
    }

    override fun onBindViewHolder(holder: habitViewHolder, position: Int) {
        val habit = habitsList[position]
        holder.title.text = habit.habitName
        Glide.with(context).load(habit.image).into(holder.image);
    }
}