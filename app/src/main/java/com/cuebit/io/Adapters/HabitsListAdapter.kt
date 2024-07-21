package com.cuebit.io.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.cuebit.io.DataModels.HabitDataModel
import com.cuebit.io.EditHabitActivity
import com.cuebit.io.R

class HabitsListAdapter(private var context: Context,private val habitsList: List<HabitDataModel>): RecyclerView.Adapter<HabitsListAdapter.HabitViewHolder>() {
    class HabitViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
        val habitNameTextView: TextView = itemView.findViewById(R.id.taskTitle)
        val habitImageView: ImageView = itemView.findViewById(R.id.imageIcon)
        val taskCategory:TextView = itemView.findViewById(R.id.taskCategory)
        val reminderCont: LinearLayout = itemView.findViewById(R.id.reminderContainer)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habitsList.size
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitsList[position]
        holder.habitImageView.visibility = View.VISIBLE
        holder.taskCategory.visibility= View.GONE
        holder.reminderCont.visibility= View.GONE
        holder.habitNameTextView.text = habit.habitName
        habit.image?.let { holder.habitImageView.setImageResource(it) }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, EditHabitActivity::class.java)
            intent.putExtra("TASK_TITLE", habit.habitName)
            context.startActivity(intent)
        }
    }
}