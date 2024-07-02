package com.timeit.app.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.timeit.Database.MyDBHelper
import com.timeit.Database.TasksDAO
import com.timeit.app.databinding.ActivityNameBinding

class NameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueButton.setOnClickListener {

                var intent = Intent(this, MainActivity::class.java);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }


        }
    }
