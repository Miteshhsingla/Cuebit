package com.cuebit.io

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.cuebit.Database.TasksDAO
import com.cuebit.io.databinding.ActivityEditTaskBinding
import com.cuebit.io.ui.MainActivity
import kotlinx.coroutines.launch

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var tasksDAO: TasksDAO
    private lateinit var categoryAdapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tasksDAO = TasksDAO(applicationContext)

        val title = intent.getStringExtra("TASK_TITLE")
        val description = intent.getStringExtra("TASK_DESCRIPTION")
        val taskDateTime = intent.getStringExtra("TASK_DATE_TIME")
        val taskCategory = intent.getStringExtra("TASK_CATEGORY")

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
        }
    }
}