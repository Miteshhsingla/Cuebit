package com.timeit.app.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.R

class TasksAdapter(private var tasksList: MutableList<TaskDataModel>, private var context: Context) : RecyclerView.Adapter<TasksAdapter.tasksViewHolder>() {

    class tasksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        val taskDateTime: TextView = itemView.findViewById(R.id.reminderTime)
        val dateTimeIcon: ImageView = itemView.findViewById(R.id.reminderIcon)
        val dateTimeContainer: LinearLayout = itemView.findViewById(R.id.reminderContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tasksViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return tasksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: tasksViewHolder, position: Int) {
        val task = tasksList[position]

        holder.taskTitle.text = task.title
        holder.taskDescription.isVisible = true
        holder.taskDescription.text = task.description
        holder.taskDateTime.text = task.dateAndTime
        holder.dateTimeContainer.isVisible = true
    }
}