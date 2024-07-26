package com.cuebit.io

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuebit.Database.TasksDAO
import com.cuebit.io.Adapters.TaskFrequencyAdapter
import com.cuebit.io.DataModels.TaskDataModel
import com.cuebit.io.databinding.ActivityEditTaskBinding
import com.cuebit.io.ui.MainActivity
import kotlinx.coroutines.launch

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var tasksDAO: TasksDAO
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var taskFrequencyAdapter: TaskFrequencyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tasksDAO = TasksDAO.getInstance(applicationContext)

        val id = intent.getStringExtra("TASK_ID")
        val title = intent.getStringExtra("TASK_TITLE")
        val description = intent.getStringExtra("TASK_DESCRIPTION")
        val taskDateTime = intent.getStringExtra("TASK_DATE_TIME")
        val taskCategory = intent.getStringExtra("TASK_CATEGORY")
        val taskFrequency = intent.getStringExtra("TASK_FREQUENCY")

        // Remove brackets and split the string into a list of days
        val cleanedString = taskFrequency?.removePrefix("[")?.removeSuffix("]")
        val daysList = cleanedString?.split(", ")?.map { it.trim() }

        // Convert to ArrayList
        val frequencyArrayList = daysList?.let { ArrayList(it) }

        // Initialize Frequency Spinner Adapter And Showing Data in Spinner
        val recyclerViewDays = binding.recyclerViewDays
        recyclerViewDays.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        taskFrequencyAdapter = TaskFrequencyAdapter(frequencyArrayList)
        binding.recyclerViewDays.adapter = taskFrequencyAdapter

        // Initialize Category Spinner Adapter
        categoryAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, mutableListOf())
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.category.adapter = categoryAdapter

        // Fetch Category Names from Database
        lifecycleScope.launch {
            val categoryNames = tasksDAO.fetchAllCategories().map { it.categoryName }
            categoryAdapter.addAll(categoryNames)
            categoryAdapter.notifyDataSetChanged()

            val position = categoryAdapter.getPosition(taskCategory)
            if (position >= 0) {
                binding.category.setSelection(position)
            }
        }

        binding.title.setText(title)
        binding.Description.setText(description)
        binding.dateAndTime.setText(taskDateTime)
        binding.backButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.SetReminderButton.setOnClickListener {
            val taskTitle = binding.title.text.toString()
            val taskDescription = binding.Description.text.toString()
            val dateAndTime = binding.dateAndTime.text.toString()
            val category = binding.category.selectedItem.toString()
            val selectedDays = taskFrequencyAdapter.getSelectedDays().toString()

            val taskDetail = TaskDataModel(id, taskTitle, taskDescription, category, dateAndTime, selectedDays)
            lifecycleScope.launch{
                try {
                    tasksDAO.editTask(id!!, taskDetail)
                    Toast.makeText(applicationContext, "Task Updated Successfully", Toast.LENGTH_LONG).show()
                    // Send broadcast to notify task insertion
                    val intentHomeFragment = Intent("com.cuebit.io.ACTION_TASK_INSERTED")
                    applicationContext.sendBroadcast(intentHomeFragment)

                    // Dismiss bottom sheet
                    val intent = Intent("com.cuebit.io.ACTION_DISMISS_BOTTOM_SHEET")
                    applicationContext.sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e("AddTaskFragment", "Error updating task", e)
                    Toast.makeText(applicationContext, "Failed to update task", Toast.LENGTH_LONG).show()
                }
            }
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}