package com.cuebit.io

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cuebit.io.databinding.ActivityEditHabitBinding

class EditHabitActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditHabitBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var title = intent.getStringExtra("TASK_TITLE")
        binding.habitTitle.setText(title)
    }
}