package com.timeit.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.timeit.app.databinding.ActivityAvatarBinding


class AvatarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAvatarBinding
    private lateinit var backgroundColors: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setClickListener(binding.av1);
        setClickListener(binding.av2);
        setClickListener(binding.av3);
        setClickListener(binding.av4);
        setClickListener(binding.av5);
        setClickListener(binding.av6);


        binding.chooseAvatarButton.setOnClickListener {
            var intent = Intent(this, NameActivity::class.java);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun setClickListener(image: ImageView) {
        image.setOnClickListener(View.OnClickListener
        { binding.preview.setImageDrawable(image.getDrawable()) })

    }
}