package com.cuebit.io.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.cuebit.io.R
import com.cuebit.io.databinding.ActivityAvatarBinding


class AvatarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAvatarBinding
    private lateinit var backgroundColors: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setClickListener(binding.av1, R.drawable.avatar1);
        setClickListener(binding.av2, R.drawable.avatar2);
        setClickListener(binding.av3, R.drawable.avatar3);
        setClickListener(binding.av4, R.drawable.avatar4);
        setClickListener(binding.av5, R.drawable.avatar3);
        setClickListener(binding.av6, R.drawable.avatar1);


        binding.chooseAvatarButton.setOnClickListener {
            var intent = Intent(this, NameActivity::class.java);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun setClickListener(image: ImageView, resourceId: Int) {
        image.setOnClickListener(View.OnClickListener{
            binding.preview.setImageDrawable(image.getDrawable())
            saveSelectedAvatar(resourceId)
        })
    }

    private fun saveSelectedAvatar(resourceId: Int) {
        val sharedPreferences = getSharedPreferences("com.cuebit.io", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("selectedAvatar", resourceId)
        editor.apply()
    }

}