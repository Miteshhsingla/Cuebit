package com.cuebit.io.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cuebit.io.DataModels.TaskDataModel
import com.cuebit.io.EditTaskActivity
import com.cuebit.io.R

class TasksAdapter(private var tasksList: MutableList<TaskDataModel>, private var context: Context) : RecyclerView.Adapter<TasksAdapter.tasksViewHolder>() {

    class tasksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        val taskDateTime: TextView = itemView.findViewById(R.id.reminderTime)
        val dateTimeIcon: ImageView = itemView.findViewById(R.id.reminderIcon)
        val dateTimeContainer: LinearLayout = itemView.findViewById(R.id.reminderContainer)
        val taskCategory: TextView = itemView.findViewById(R.id.taskCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tasksViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return tasksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    fun getItem(position: Int): TaskDataModel {
        return tasksList[position]
    }

    fun removeItem(position: Int) {
        tasksList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: tasksViewHolder, position: Int) {
        val task = tasksList[position]

        holder.taskTitle.text = task.title
        holder.taskDescription.isVisible = true
        holder.taskDescription.text = task.description
        holder.dateTimeContainer.isVisible = true
        val dateTime = task.dateAndTime
        val time = dateTime!!.split(" ")[1]
        holder.taskDateTime.text = time
        holder.taskCategory.text = task.category


        holder.itemView.setOnClickListener {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            intent.putExtra("TASK_TITLE", task.title)
            intent.putExtra("TASK_DESCRIPTION", task.description)
            intent.putExtra("TASK_DATE_TIME", task.dateAndTime)
            intent.putExtra("TASK_CATEGORY", task.category)
            intent.putExtra("TASK_FREQUENCY", task.frequency)
            context.startActivity(intent)
        }


    }

    fun updateTasks(tasksList: List<TaskDataModel>) {
        this.tasksList.clear()
        this.tasksList.addAll(tasksList)
        notifyDataSetChanged()
    }
}