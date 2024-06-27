package com.timeit.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.timeit.app.databinding.ActivityAvatarBinding
import com.timeit.app.databinding.ActivityGetStartedBinding

class AvatarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAvatarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chooseAvatarButton.setOnClickListener {
            var intent = Intent(this,NameActivity::class.java);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}