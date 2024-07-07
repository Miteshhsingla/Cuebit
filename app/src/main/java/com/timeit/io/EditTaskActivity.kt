package com.timeit.io

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.timeit.io.databinding.ActivityEditTaskBinding
import com.timeit.io.ui.MainActivity

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val title = intent.getStringExtra("TASK_TITLE")
        val description = intent.getStringExtra("TASK_DESCRIPTION")
        val taskDateTime = intent.getStringExtra("TASK_DATE_TIME")
        val taskCategory = intent.getStringExtra("TASK_CATEGORY")

        binding.title.setText(title)
        binding.Description.setText(description)
        binding.dateAndTime.setText(taskDateTime)
        binding.backButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}