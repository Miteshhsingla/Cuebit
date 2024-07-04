package com.timeit.app.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.timeit.Database.MyDBHelper
import com.timeit.Database.TasksDAO
import com.timeit.app.databinding.ActivityNameBinding

class NameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNameBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("com.timeit.app", MODE_PRIVATE)
        val userImage = sharedPreferences.getInt("selectedAvatar", 0)
        binding.userImage.setImageResource(userImage)

        binding.continueButton.setOnClickListener {

            val name = binding.nameField.text.toString().trim()

            if(name.isEmpty()){
                Toast.makeText(this@NameActivity, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                // Setting the flag indicating that the setup is complete
                val editor = sharedPreferences.edit()
                editor.putBoolean("isFirstTime", false)
                editor.putString("userName", name)
                editor.apply()

                var intent = Intent(this, MainActivity::class.java);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

    }
}
